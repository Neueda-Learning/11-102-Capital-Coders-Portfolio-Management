FROM nginx:1.27-alpine

COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY index.html /usr/share/nginx/html/index.html
COPY dashboard /usr/share/nginx/html/dashboard
COPY login /usr/share/nginx/html/login
COPY portfolio /usr/share/nginx/html/portfolio

EXPOSE 80

