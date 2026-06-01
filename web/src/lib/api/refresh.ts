import axios from "axios";
import { env } from "~/env";
import type { AuthTokens } from "~/types/auth-tokens";

/**
 * Bare axios instance — no interceptors, avoids infinite loop
 */
export async function refreshTokens(token: string): Promise<AuthTokens> {
  return await axios
    .post<AuthTokens>(`${env.VITE_API_URL}/auth/refresh`, { token })
    .then((res) => res.data);
}
