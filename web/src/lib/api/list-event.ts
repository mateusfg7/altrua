import type { NgoEvent, NgoEventListFilters } from "~/types/ngo-event";
import type { Paginated, PaginationParams } from "~/types/pagination";
import { apiClient } from "../api-client";



export async function listEvents(
  pagination?: PaginationParams & NgoEventListFilters
) {



  return await apiClient
    .get<Paginated<NgoEvent>>("/eventos", { params: pagination })
    .then((res) => res.data);
}
