export type NgoEvent = {
  id: string;
  title: string;
  description: string;
  slug: string;
  coverUrl: string;
  externalLink: string | null;
  donationInfo: string | null;
  donationExternalLink: string | null;
  acceptsVolunteers: boolean;
  maxVolunteers: number | null;
  latitude: number;
  longitude: number;
  addressLabel: string;
  startsAt: string;
  endsAt: string;
  tags: string[];
};

export type NgoEventListFilters = {
  tag?: string;
  status?: "PUBLISHED" | "ONGOING" | "FINISHED" | "CANCELED";
  acceptsVolunteers?: boolean;
};
