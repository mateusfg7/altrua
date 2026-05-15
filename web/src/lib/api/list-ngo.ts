import { apiClient } from "~/lib/api-client";
import type { Ngo } from "~/types/ngo";
import type { Paginated, PaginationParams } from "~/types/pagination";

export async function listNGOs(
  pagination?: PaginationParams
): Promise<Paginated<Ngo>> {
  return await apiClient
    .get<Paginated<Ngo>>("/ongs", { params: pagination })
    .then((res) => res.data);
}
