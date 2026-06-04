import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { createNgo } from "~/lib/api/create-ngo";
import type { ApiError } from "~/types/api-error";
import type { CreateNgoData } from "~/types/create-ngo";
import type { Ngo } from "~/types/ngo";
import { NGO_LIST_QUERY_KEY } from "./use-ngo-list";

export function useCreateNgo() {
  const queryClient = useQueryClient();

  return useMutation<Ngo, AxiosError<ApiError>, CreateNgoData>({
    mutationFn: createNgo,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: NGO_LIST_QUERY_KEY });
    },
  });
}
