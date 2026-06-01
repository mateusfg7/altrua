import type { AuthTokens } from "~/types/auth-tokens";
import type { SignUpData } from "~/types/sign-up";
import { apiClient } from "../api-client";

export async function signUp(data: SignUpData) {
  return await apiClient
    .post<AuthTokens>("/auth/signup", data)
    .then((response) => response.data);
}
