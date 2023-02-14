FROM node:16.13.2-alpine AS mgr-front
WORKDIR /app
COPY . .
RUN npm ci && npm run build

FROM nginx:latest AS ngi
COPY --from=mgr-front app/dist/mgr-front /usr/share/nginx/html
EXPOSE 80
