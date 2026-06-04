import { type QueryKey, useInfiniteQuery } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { listNGOs } from "~/lib/api/list-ngo";
import type { ApiError } from "~/types/api-error";
import type { Ngo } from "~/types/ngo";
import type { Paginated } from "~/types/pagination";

export const INFINITE_NGO_LIST_QUERY_KEY = ["ngo", "infinite-list"];

const PAGE_SIZE = 12;

export function useInfiniteNgoList() {
  return useInfiniteQuery<
    Paginated<Ngo>,
    AxiosError<ApiError>,
    Ngo[],
    QueryKey,
    number
  >({
    queryKey: INFINITE_NGO_LIST_QUERY_KEY,
    queryFn: ({ pageParam }) => listNGOs({ page: pageParam, size: PAGE_SIZE }),
    initialPageParam: 0,
    getNextPageParam: (lastPage) => {
      const nextPage = lastPage.page.number + 1;
      return nextPage < lastPage.page.totalPages ? nextPage : undefined;
    },
    select: (data) => data.pages.flatMap((page) => page.content),
    refetchOnWindowFocus: false,
  });
}
