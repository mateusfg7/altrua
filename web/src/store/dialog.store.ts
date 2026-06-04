import { create } from "zustand";
import type { Ngo } from "~/types/ngo";

import type { NgoEvent } from "~/types/ngo-event";

type Dialog = null | "ong-details" | "event-details" | "create-ngo";

type DialogStore = {
  dialog: Dialog;
  setDialog: (d: Dialog) => void;

  ong: null | Ngo;
  setOng: (o: null | Ngo) => void;

  event: null | NgoEvent;
  setEvent: (o: null | NgoEvent) => void;

  close: () => void;
};

export const useDialogStore = create<DialogStore>((set) => ({
  dialog: null,
  setDialog: (dialog) => set({ dialog }),

  ong: null,
  setOng: (ong) => set({ ong }),

  event: null,
  setEvent: (event) => set({ event }),

  close: () => set({ dialog: null, ong: null, event: null }),
}));
