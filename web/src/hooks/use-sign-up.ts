import { useMutation } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { signUp } from "~/lib/api/sign-up";
import type { ApiError } from "~/types/api-error";
import type { AuthTokens } from "~/types/auth-tokens";
import type { SignUpData } from "~/types/sign-up";

export function useSignUp() {
  return useMutation<AuthTokens, AxiosError<ApiError>, SignUpData>({
    mutationFn: signUp,
  });
}
