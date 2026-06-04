import { type QueryKey, useInfiniteQuery } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { listEvents } from "~/lib/api/list-event";
import type { ApiError } from "~/types/api-error";
import type { NgoEvent, NgoEventListFilters } from "~/types/ngo-event";
import type { Paginated } from "~/types/pagination";

export const INFINITE_NGO_EVENT_LIST_QUERY_KEY = [
  "ngo",
  "event",
  "infinite-list",
];

const PAGE_SIZE = 10;

export function useInfiniteNgoEventList(filters?: NgoEventListFilters) {
  return useInfiniteQuery<
    Paginated<NgoEvent>,
    AxiosError<ApiError>,
    NgoEvent[],
    QueryKey,
    number
  >({
    queryKey: [...INFINITE_NGO_EVENT_LIST_QUERY_KEY, filters],
    queryFn: ({ pageParam }) =>
      listEvents({ ...filters, page: pageParam, size: PAGE_SIZE }),
    initialPageParam: 0,
    getNextPageParam: (lastPage) => {
      const nextPage = lastPage.page.number + 1;
      return nextPage < lastPage.page.totalPages ? nextPage : undefined;
    },
    select: (data) => data.pages.flatMap((page) => page.content),
    refetchOnWindowFocus: false,
  });
}
