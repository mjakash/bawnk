# ----- Stage 1: Build -----
# Use a Node.js image to build the React app
FROM node:18-alpine AS build
WORKDIR /app
COPY package.json ./
COPY package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

# ----- Stage 2: Serve -----
# Use a lightweight Nginx image to serve the static files
FROM nginx:1.25-alpine
# Copy the built app from the 'build' stage
COPY --from=build /app/dist /usr/share/nginx/html
# Copy the Nginx config file (we will create this next)
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]