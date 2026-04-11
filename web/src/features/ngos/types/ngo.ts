export type NGO = {
  id: string;
  name: string;
  slug: string;
  cnpj: string | null;
  description: string | null;
  email: string;
  phone: string | null;
  category: string;
  status: "ACTIVE";
  logoUrl: string | null;
  bannerUrl: string | null;
  donationInfo: string | null;
  activeEventCount: number;
  latitude: number | null;
  longitude: number | null;
  createdAt: string;
};
