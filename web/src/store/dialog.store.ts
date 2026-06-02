import { create } from "zustand";
import type { Ngo } from "~/types/ngo";

type Dialog = null | "ong-details" | "event-details";

type DialogStore = {
  dialog: Dialog;
  setDialog: (d: Dialog) => void;

  ong: null | Ngo;
  setOng: (o: null | Ngo) => void;

  close: () => void;
};

export const useDialogStore = create<DialogStore>((set) => ({
  dialog: null,
  setDialog: (dialog) => set({ dialog }),

  ong: null,
  setOng: (ong) => set({ ong }),

  close: () => set({ dialog: null, ong: null }),
}));
