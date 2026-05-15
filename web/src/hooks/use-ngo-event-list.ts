import { useQuery } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { listEvents } from "~/shared/lib/api/list-event";
import type { ApiError } from "~/shared/types/api-error";
import type { Paginated, PaginationParams } from "~/shared/types/pagination";
import type { NGOEvent, NGOEventListFilters } from "../shared/types/ngo-event";

export const NGO_EVENT_LIST_QUERY_KEY = ["ngo", "event", "list"];

export function useNGOEventList(
  filters?: PaginationParams & NGOEventListFilters
) {
  return useQuery<Paginated<NGOEvent>, AxiosError<ApiError>>({
    queryKey: [...NGO_EVENT_LIST_QUERY_KEY, filters],
    queryFn: () => listEvents(filters),
  });
}
