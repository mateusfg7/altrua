import { apiClient } from "~/lib/api-client";
import type { Paginated, PaginationParams } from "~/types/pagination";
import type { NGO } from "./types/ngo";

export async function listNGOs(
  pagination?: PaginationParams
): Promise<Paginated<NGO>> {
  return await apiClient
    .get<Paginated<NGO>>("/ongs", { params: pagination })
    .then((res) => res.data);

  // return {
  //   content: [
  //     {
  //       id: "f43ebb05-dfa5-4e36-bb27-4f8a82fbc1d8",
  //       name: "Instituto Esperança Viva",
  //       slug: "instituto-esperanca-viva",
  //       cnpj: "00355381000121",
  //       description:
  //         "Promovemos educação e capacitação profissional para jovens em situação de vulnerabilidade social.",
  //       email: "contato@esperanca.com.br",
  //       phone: null,
  //       category: "Educação",
  //       status: "ACTIVE",
  //       logoUrl: null,
  //       bannerUrl:
  //         "https://images.unsplash.com/photo-1509062522246-3755977927d7?w=600&h=400&fit=crop",
  //       donationInfo: null,
  //       activeEventCount: 3,
  //       latitude: -23.579_033_89,
  //       longitude: -46.639_843_04,
  //       createdAt: "2026-04-04T00:03:46.797441Z",
  //     },
  //     {
  //       id: "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  //       name: "Patas Felizes",
  //       slug: "patas-felizes",
  //       cnpj: null,
  //       description:
  //         "Resgatamos, cuidamos e promovemos a adoção responsável de animais abandonados.",
  //       email: "contato@patasfelizes.org.br",
  //       phone: null,
  //       category: "Animais",
  //       status: "ACTIVE",
  //       logoUrl: null,
  //       bannerUrl:
  //         "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=600&h=400&fit=crop",
  //       donationInfo: null,
  //       latitude: null,
  //       longitude: null,
  //       activeEventCount: 5,
  //       createdAt: "2026-04-04T00:03:46.797441Z",
  //     },
  //     {
  //       id: "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
  //       name: "Mãos que Alimentam",
  //       slug: "maos-que-alimentam",
  //       cnpj: null,
  //       description:
  //         "Distribuímos refeições nutritivas para pessoas em situação de rua e famílias carentes.",
  //       email: "contato@maosquealimentam.org.br",
  //       phone: null,
  //       category: "Alimentação",
  //       status: "ACTIVE",
  //       logoUrl: null,
  //       bannerUrl:
  //         "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?w=600&h=400&fit=crop",
  //       donationInfo: null,
  //       latitude: null,
  //       longitude: null,
  //       activeEventCount: 2,
  //       createdAt: "2026-04-04T00:03:46.797441Z",
  //     },
  //     {
  //       id: "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
  //       name: "Verde Vida",
  //       slug: "verde-vida",
  //       cnpj: null,
  //       description:
  //         "Trabalhamos pela preservação ambiental através de reflorestamento e educação ecológica.",
  //       email: "contato@verdevida.org.br",
  //       phone: null,
  //       category: "Meio Ambiente",
  //       status: "ACTIVE",
  //       logoUrl: null,
  //       bannerUrl:
  //         "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=600&h=400&fit=crop",
  //       donationInfo: null,
  //       latitude: null,
  //       longitude: null,
  //       activeEventCount: 4,
  //       createdAt: "2026-04-04T00:03:46.797441Z",
  //     },
  //   ],
  //   page: {
  //     size: 10,
  //     number: 0,
  //     totalElements: 4,
  //     totalPages: 1,
  //   },
  // };
}
