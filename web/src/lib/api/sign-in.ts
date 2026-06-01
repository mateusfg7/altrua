import type { AuthTokens } from "~/types/auth-tokens";
import type { SignInData } from "~/types/sign-in";
import { apiClient } from "../api-client";

export async function signIn(data: SignInData) {
  return await apiClient
    .post<AuthTokens>("/auth/login", data)
    .then((response) => response.data);
}
