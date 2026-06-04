import { apiClient } from "~/lib/api-client";
import type { CreateNgoData } from "~/types/create-ngo";
import type { Ngo } from "~/types/ngo";

export async function createNgo(data: CreateNgoData): Promise<Ngo> {
  return await apiClient
    .post<Ngo>("/ngos", data)
    .then((res) => res.data);
}
