import { create } from "zustand";

export type FeedSection = "eventos" | "ongs";

type FeedStore = {
  /** Which collection the authenticated feed is currently showing. */
  section: FeedSection;
  setSection: (section: FeedSection) => void;
};

/**
 * Drives the authenticated feed navigation from the app sidebar so the sidebar
 * and the feed content stay in sync without prop drilling.
 */
export const useFeedStore = create<FeedStore>((set) => ({
  section: "eventos",
  setSection: (section) => set({ section }),
}));
