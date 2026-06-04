/** biome-ignore-all lint/correctness/useImageSize: <explanation> */
import {
  Calendar03Icon,
  Clock01Icon,
  HeartCheckIcon,
  LinkSquare01Icon,
  Location01Icon,
  Tag01Icon,
  UserGroupIcon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { useNavigate } from "@tanstack/react-router";
import { toast } from "sonner";
import { useVolunteerSubscription } from "~/hooks/use-volunteer-subscription";
import { useAuthStore } from "~/store/auth.store";
import { useDialogStore } from "~/store/dialog.store";
import { Badge } from "../ui/badge";
import { Button } from "../ui/button";
import { Dialog, DialogContent, DialogTitle } from "../ui/dialog";
import { Separator } from "../ui/separator";

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function EventDetails() {
  const dialog = useDialogStore((s) => s.dialog);
  const event = useDialogStore((s) => s.event);
  const close = useDialogStore((s) => s.close);
  const accessToken = useAuthStore((s) => s.accessToken);
  const navigate = useNavigate();
  const { mutate: subscribe, isPending } = useVolunteerSubscription();

  if (!event) {
    return null;
  }

  function handleSubscribe() {
    if (!event) {
      return;
    }

    if (!accessToken) {
      close();
      navigate({ to: "/sign-in" });
      return;
    }

    subscribe(
      { ongId: event.ongId, eventId: event.id },
      {
        onSuccess: () => {
          toast.success("Inscrição realizada com sucesso!");
          close();
        },
        onError: (error) => {
          toast.error(
            error.response?.data?.detail ?? "Ocorreu um erro ao se inscrever"
          );
        },
      }
    );
  }

  const sameDay =
    new Date(event.startsAt).toDateString() ===
    new Date(event.endsAt).toDateString();

  return (
    <Dialog onOpenChange={close} open={dialog === "event-details"}>
      <DialogContent className="max-w-lg gap-0 overflow-hidden p-0">
        {/* Cover */}
        <div className="relative h-44 overflow-hidden bg-muted">
          <img
            alt={event.title}
            className="h-full w-full object-cover"
            src={event.coverUrl}
          />
          <div className="absolute inset-0 bg-linear-to-t from-black/60 to-transparent" />

          {event.acceptsVolunteers && (
            <div className="absolute top-3 left-3">
              <Badge className="gap-1 border-0 bg-emerald-700 text-white">
                <HugeiconsIcon icon={UserGroupIcon} size={12} />
                Voluntariado
              </Badge>
            </div>
          )}

          <div className="absolute right-4 bottom-3 left-4">
            <DialogTitle className="font-medium text-lg text-white leading-snug drop-shadow">
              {event.title}
            </DialogTitle>
          </div>
        </div>

        {/* Date + Location */}
        <div className="space-y-2.5 px-6 py-4 text-sm">
          <div className="flex items-start gap-2 text-muted-foreground">
            <HugeiconsIcon
              className="mt-0.5 shrink-0"
              icon={Calendar03Icon}
              size={15}
            />
            <span>
              {formatDate(event.startsAt)}
              {!sameDay && (
                <>
                  {" "}
                  →{" "}
                  <span className="inline-flex items-center gap-1">
                    <HugeiconsIcon icon={Clock01Icon} size={13} />
                    {formatDate(event.endsAt)}
                  </span>
                </>
              )}
              {sameDay && (
                <span className="ml-1 text-foreground">
                  →{" "}
                  {new Date(event.endsAt).toLocaleTimeString("pt-BR", {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </span>
              )}
            </span>
          </div>

          <div className="flex items-start gap-2 text-muted-foreground">
            <HugeiconsIcon
              className="mt-0.5 shrink-0"
              icon={Location01Icon}
              size={15}
            />
            <a
              className="text-blue-600 hover:underline"
              href={`https://maps.google.com/?q=${event.latitude},${event.longitude}`}
              rel="noreferrer"
              target="_blank"
            >
              {event.addressLabel}
            </a>
          </div>
        </div>

        <Separator />

        {/* Description */}
        <p className="px-6 py-4 text-muted-foreground text-sm leading-relaxed">
          {event.description}
        </p>

        {/* Tags */}
        {event.tags?.length > 0 && (
          <>
            <Separator />
            <div className="flex flex-wrap items-center gap-2 px-6 py-4">
              <HugeiconsIcon
                className="shrink-0 text-muted-foreground"
                icon={Tag01Icon}
                size={14}
              />
              {event.tags.map((tag) => (
                <Badge className="text-xs" key={tag} variant="secondary">
                  {tag}
                </Badge>
              ))}
            </div>
          </>
        )}

        {/* Volunteers stat */}
        {event.acceptsVolunteers && (
          <>
            <Separator />
            <div className="bg-muted/50 px-6 py-4">
              <div className="flex items-center justify-between text-sm">
                <span className="flex items-center gap-2 text-muted-foreground">
                  <HugeiconsIcon icon={UserGroupIcon} size={15} />
                  Vagas para voluntários
                </span>
                <span className="font-medium">
                  {event.maxVolunteers ?? "Ilimitado"}
                </span>
              </div>
            </div>
          </>
        )}

        {/* Donation info */}
        {event.donationInfo && (
          <>
            <Separator />
            <div className="space-y-2 px-6 py-4">
              <p className="font-medium text-muted-foreground text-xs uppercase tracking-wide">
                Informações de doação
              </p>
              <p className="text-muted-foreground text-sm leading-relaxed">
                {event.donationInfo}
              </p>
            </div>
          </>
        )}

        {/* Actions */}
        <Separator />
        <div className="flex gap-2 px-6 py-4">
          <Button
            className="flex-1 bg-emerald-700 text-white hover:bg-emerald-800"
            disabled={isPending}
            onClick={handleSubscribe}
          >
            <HugeiconsIcon className="mr-2" icon={UserGroupIcon} size={15} />
            {isPending ? "Inscrevendo..." : "Participar"}
          </Button>

          {event.donationExternalLink && (
            <Button asChild className="flex-1" variant="outline">
              <a
                href={event.donationExternalLink}
                rel="noreferrer"
                target="_blank"
              >
                <HugeiconsIcon
                  className="mr-2"
                  icon={HeartCheckIcon}
                  size={15}
                />
                Doar
              </a>
            </Button>
          )}

          {event.externalLink && (
            <Button asChild size="icon" variant="outline">
              <a
                aria-label="Link externo"
                href={event.externalLink}
                rel="noreferrer"
                target="_blank"
              >
                <HugeiconsIcon icon={LinkSquare01Icon} size={15} />
              </a>
            </Button>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}
