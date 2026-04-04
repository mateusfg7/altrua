import { useQuery } from "@tanstack/react-query";
import { listNGOs } from "../api";

export const NGO_LIST_QUERY_KEY = ["ngo", "list"];

export function useNgoList() {
  return useQuery({
    queryKey: NGO_LIST_QUERY_KEY,
    queryFn: listNGOs,
    retry: false,
  });
}
