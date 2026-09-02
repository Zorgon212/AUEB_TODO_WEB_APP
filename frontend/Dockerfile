# ---- build stage ----
FROM node:22-alpine AS build
WORKDIR /app

COPY package.json package-lock.json .
RUN npm ci

COPY . .
RUN npm run build

# ---- run stage ----
FROM node:22-alpine
WORKDIR /app
ENV NODE_ENV=production
ENV PORT=3000

# adapter-node's build output is self-contained: the built server + its own
# package.json (so we can install just its runtime deps, not the dev toolchain)
COPY --from=build /app/build ./build
COPY --from=build /app/package.json ./package.json
RUN npm install --omit=dev

EXPOSE 3000
CMD ["node", "build"]
