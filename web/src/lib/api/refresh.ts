import axios from "axios";
import { API_BASE_PATH } from "~/lib/api-client";
import type { AuthTokens } from "~/types/auth-tokens";

/**
 * Bare axios instance — no interceptors, avoids infinite loop
 */
export async function refreshTokens(token: string): Promise<AuthTokens> {
  return await axios
    .post<AuthTokens>(`${API_BASE_PATH}/auth/refresh`, { token })
    .then((res) => res.data);
}
