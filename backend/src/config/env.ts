const getEnv = (key: string, defaultValue?: string): string => {
  const value = process.env[key] || defaultValue;
  if (value === undefined) {
    throw Error(`Missing String environment variable for ${key}`);
  }
  return value;
};
export const NODE_ENV = getEnv("NODE_ENV", "development");
export const PORT = getEnv("PORT", "4004");
export const JWT_SECRET = getEnv("JWT_SECRET");
export const JWT_REFRESH_SECRET = getEnv("JWT_REFRESH_SECRET");
export const GOOGLE_CLIENT_ID = getEnv("GOOGLE_CLIENT_ID");
export const DATABASE_URL = getEnv("DATABASE_URL");
export const REDIS_URL = getEnv("REDIS_URL", "redis://localhost:6379");
