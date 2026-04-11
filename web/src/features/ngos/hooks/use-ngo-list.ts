import { useQuery } from "@tanstack/react-query";
import type { PaginationParams } from "~/types/pagination";
import { listNGOs } from "../api";

export const NGO_LIST_QUERY_KEY = ["ngo", "list"];

export function useNgoList(pagination?: PaginationParams) {
  return useQuery({
    queryKey: [...NGO_LIST_QUERY_KEY, pagination],
    queryFn: () => listNGOs(pagination),
    retry: false,
  });
}
