/** biome-ignore-all lint/correctness/useImageSize: <explanation> */
import {
  Building04Icon,
  Calendar03Icon,
  FavouriteIcon,
  Location01Icon,
  Mail01Icon,
  Share01Icon,
  SmartPhone01Icon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { useDialogStore } from "~/store/dialog.store";
import { Badge } from "../ui/badge";
import { Button } from "../ui/button";
import { Dialog, DialogContent, DialogTitle } from "../ui/dialog";
import { Separator } from "../ui/separator";

export function OngDetails() {
  const dialog = useDialogStore((s) => s.dialog);
  const ong = useDialogStore((s) => s.ong);
  const close = useDialogStore((s) => s.close);

  if (!ong) {
    return null;
  }

  const initials = ong.name
    .split(" ")
    .slice(0, 2)
    .map((w) => w[0])
    .join("")
    .toUpperCase();

  return (
    <Dialog onOpenChange={close} open={dialog === "ong-details"}>
      <DialogContent className="max-w-lg gap-0 overflow-hidden p-0">
        {/* Banner */}
        <div className="relative h-40 bg-muted">
          {ong.bannerUrl ? (
            <img
              alt=""
              className="h-full w-full object-cover"
              src={ong.bannerUrl}
            />
          ) : (
            <div className="absolute inset-0 bg-linear-to-br from-emerald-600 to-emerald-800 opacity-85" />
          )}

          <div className="absolute right-3 bottom-3">
            <Badge
              className="border-white/30 bg-black/20 text-white backdrop-blur-sm"
              variant="secondary"
            >
              {ong.category}
            </Badge>
          </div>

          <div className="absolute -bottom-7 left-6">
            {ong.logoUrl ? (
              <img
                alt={ong.name}
                className="h-14 w-14 rounded-full border-2 border-background object-cover"
                src={ong.logoUrl}
              />
            ) : (
              <div className="flex h-14 w-14 items-center justify-center rounded-full border-2 border-background bg-emerald-100 font-medium text-emerald-700 text-lg">
                {initials}
              </div>
            )}
          </div>
        </div>

        {/* Header */}
        <div className="px-6 pt-10 pb-4">
          <div className="flex items-start justify-between gap-3">
            <div>
              <DialogTitle className="font-medium text-lg leading-tight">
                {ong.name}
              </DialogTitle>
              <p className="mt-0.5 text-muted-foreground text-xs">{ong.slug}</p>
            </div>
            <Badge
              className="shrink-0 border-emerald-200 bg-emerald-50 text-emerald-700"
              variant="outline"
            >
              ● Ativa
            </Badge>
          </div>
        </div>

        {ong.description && (
          <>
            <Separator />
            <p className="px-6 py-4 text-muted-foreground text-sm leading-relaxed">
              {ong.description}
            </p>
          </>
        )}

        {/* Stats */}
        <Separator />
        <div className="grid grid-cols-2 gap-3 px-6 py-4">
          <div className="rounded-lg bg-muted px-4 py-3">
            <p className="mb-1 text-[11px] text-muted-foreground uppercase tracking-wide">
              Eventos ativos
            </p>
            <p className="font-medium text-2xl">{ong.activeEventCount}</p>
          </div>
          <div className="rounded-lg bg-muted px-4 py-3">
            <p className="mb-1 text-[11px] text-muted-foreground uppercase tracking-wide">
              Categoria
            </p>
            <p className="font-medium text-sm">{ong.category}</p>
          </div>
        </div>

        {/* Details */}
        <Separator />
        <div className="space-y-3 px-6 py-4 text-sm">
          <div className="flex items-center justify-between">
            <span className="flex items-center gap-2 text-muted-foreground">
              <HugeiconsIcon icon={Location01Icon} size={15} /> Localização
            </span>
            <span>São Paulo, SP</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="flex items-center gap-2 text-muted-foreground">
              <HugeiconsIcon icon={Mail01Icon} size={15} /> Email
            </span>
            <a
              className="text-blue-600 hover:underline"
              href={`mailto:${ong.email}`}
            >
              {ong.email}
            </a>
          </div>
          {ong.phone && (
            <div className="flex items-center justify-between">
              <span className="flex items-center gap-2 text-muted-foreground">
                <HugeiconsIcon icon={SmartPhone01Icon} size={15} />
                Telefone
              </span>
              <span>{ong.phone}</span>
            </div>
          )}
          {ong.cnpj && (
            <div className="flex items-center justify-between">
              <span className="flex items-center gap-2 text-muted-foreground">
                <HugeiconsIcon icon={Building04Icon} size={15} /> CNPJ
              </span>
              <span>{ong.cnpj}</span>
            </div>
          )}
        </div>

        {/* Actions */}
        <Separator />
        <div className="flex gap-2 px-6 py-4">
          <Button className="flex-1 bg-emerald-700 text-white hover:bg-emerald-800">
            <HugeiconsIcon icon={FavouriteIcon} size={15} /> Contribuir
          </Button>
          <Button className="flex-1" variant="outline">
            <HugeiconsIcon icon={Calendar03Icon} size={15} /> Ver eventos
          </Button>
          <Button size="icon" variant="outline">
            <HugeiconsIcon icon={Share01Icon} size={15} />
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
