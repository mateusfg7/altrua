import { useMutation } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { signIn } from "~/lib/api/sign-in";
import type { ApiError } from "~/types/api-error";
import type { AuthTokens } from "~/types/auth-tokens";
import type { SignInData } from "~/types/sign-in";

export function useSignIn() {
  return useMutation<AuthTokens, AxiosError<ApiError>, SignInData>({
    mutationFn: signIn,
  });
}
