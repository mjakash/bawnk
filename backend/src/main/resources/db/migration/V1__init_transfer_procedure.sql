-- This function performs an atomic transfer and returns whether it was successful.
CREATE OR REPLACE FUNCTION execute_transfer(
    from_account_id BIGINT,
    to_account_id BIGINT,
    transfer_amount DECIMAL(19, 2),
    transfer_description TEXT
) RETURNS BOOLEAN AS $$
DECLARE
    from_balance DECIMAL(19, 2);
BEGIN
    -- 1. Get the sender's current balance and lock the row for this transaction
    SELECT balance INTO from_balance FROM account WHERE id = from_account_id FOR UPDATE;

    -- 2. Check if the sender has sufficient funds
    IF from_balance IS NULL OR from_balance < transfer_amount THEN
        RETURN FALSE; -- Insufficient funds or sender account not found
    END IF;

    -- 3. Check if the recipient account exists
    IF NOT EXISTS (SELECT 1 FROM account WHERE id = to_account_id) THEN
        RETURN FALSE; -- Recipient account not found
    END IF;

    -- 4. Debit the sender's account
    UPDATE account
    SET balance = balance - transfer_amount
    WHERE id = from_account_id;

    -- 5. Credit the recipient's account
    UPDATE account
    SET balance = balance + transfer_amount
    WHERE id = to_account_id;

    -- 6. Log the transaction in the history table
    INSERT INTO transaction_history (from_account_id, to_account_id, amount, timestamp, description)
    VALUES (from_account_id, to_account_id, transfer_amount, NOW(), transfer_description);

    -- 7. If all steps succeed, commit the transaction
    RETURN TRUE;

EXCEPTION
    -- If any error occurs (e.g., constraint violation), roll back everything
    WHEN OTHERS THEN
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;