import {
  Calendar03Icon,
  Coins01Icon,
  FavouriteIcon,
  Location01Icon,
  UserGroupIcon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { useNavigate } from "@tanstack/react-router";
import { format } from "date-fns";
import { ptBR } from "date-fns/locale/pt-BR";
import { toast } from "sonner";
import { Badge } from "~/components/ui/badge";
import { Button } from "~/components/ui/button";
import { Card, CardContent } from "~/components/ui/card";
import { useVolunteerSubscription } from "~/hooks/use-volunteer-subscription";
import { useAuthStore } from "~/store/auth.store";
import { useDialogStore } from "~/store/dialog.store";
import type { NgoEvent } from "~/types/ngo-event";

export function EventCard(data: NgoEvent) {
  const ongName = "ONG Exemplo";

  const setDialog = useDialogStore((s) => s.setDialog);
  const setEvent = useDialogStore((s) => s.setEvent);
  const accessToken = useAuthStore((s) => s.accessToken);
  const navigate = useNavigate();
  const { mutate: subscribe, isPending } = useVolunteerSubscription();

  const {
    id,
    ongId,
    acceptsVolunteers,
    addressLabel,
    coverUrl,
    description,
    donationExternalLink,
    donationInfo,
    isEnrolled,
    maxVolunteers,
    currentVolunteers,
    startsAt,
    title,
  } = data;

  function open() {
    setDialog("event-details");
    setEvent(data);
  }

  function handleSubscribe() {
    if (!accessToken) {
      navigate({ to: "/sign-in" });
      return;
    }

    subscribe(
      { ongId, eventId: id },
      {
        onSuccess: () => {
          toast.success("Inscrição realizada com sucesso!");
        },
        onError: (error) => {
          toast.error(
            error.response?.data?.detail ?? "Ocorreu um erro ao se inscrever"
          );
        },
      }
    );
  }

  const progress =
    maxVolunteers && maxVolunteers > 0
      ? Math.min((currentVolunteers / maxVolunteers) * 100, 100)
      : 0;

  return (
    <Card className="group overflow-hidden p-0 transition-all">
      <div className="flex flex-col md:flex-row">
        <div className="relative aspect-video w-full overflow-hidden md:aspect-square md:w-48 lg:w-56">
          {/** biome-ignore lint/correctness/useImageSize: Image comes from external source */}
          <img
            alt={title}
            className="size-full object-cover transition-transform duration-300 group-hover:scale-105"
            src={coverUrl}
          />
          <div className="absolute top-3 left-3 flex gap-2">
            {acceptsVolunteers && (
              <Badge className="bg-primary/90 text-primary-foreground backdrop-blur-sm">
                <HugeiconsIcon className="mr-1 size-3" icon={UserGroupIcon} />
                Voluntariado
              </Badge>
            )}
            {(donationInfo || donationExternalLink) && (
              <Badge
                className="bg-card/90 backdrop-blur-sm"
                variant="secondary"
              >
                <HugeiconsIcon className="mr-1 size-3" icon={Coins01Icon} />
                Doação
              </Badge>
            )}
          </div>
        </div>

        <CardContent className="flex flex-1 flex-col justify-between p-5">
          <div>
            <div className="mb-2 flex items-center gap-2">
              <HugeiconsIcon
                className="size-4 text-primary"
                icon={FavouriteIcon}
              />
              <span className="font-medium text-primary text-sm">
                {ongName}
              </span>
            </div>

            <h3 className="line-clamp-1 font-semibold text-xl">{title}</h3>
            <p className="mt-2 line-clamp-2 text-muted-foreground text-sm">
              {description}
            </p>

            <div className="mt-4 flex flex-wrap items-center gap-4 text-muted-foreground text-sm">
              <div className="flex items-center gap-1">
                <HugeiconsIcon className="size-4" icon={Calendar03Icon} />
                <span>
                  {format(startsAt, "dd MMM, yyyy · HH:mm", { locale: ptBR })}
                </span>
              </div>
              <div className="flex items-center gap-1">
                <HugeiconsIcon className="size-4" icon={Location01Icon} />
                <span>{addressLabel}</span>
              </div>
            </div>
          </div>

          {acceptsVolunteers && (
            <div className="mt-4">
              <div className="mb-2 flex items-center justify-between text-sm">
                <span className="text-muted-foreground">Voluntários</span>
                <span className="font-medium">
                  {currentVolunteers}/{maxVolunteers}
                </span>
              </div>
              <div className="h-2 w-full overflow-hidden rounded-full bg-muted">
                <div
                  className="h-full rounded-full bg-primary transition-all"
                  style={{ width: `${progress}%` }}
                />
              </div>
            </div>
          )}

          <div className="mt-4 flex items-center justify-end gap-3">
            <Button onClick={open} variant="outline">
              Detalhes
            </Button>
            {acceptsVolunteers && !isEnrolled && (
              <Button disabled={isPending} onClick={handleSubscribe} size="lg">
                {isPending ? "Inscrevendo..." : "Participar"}
              </Button>
            )}
          </div>
        </CardContent>
      </div>
    </Card>
  );
}
