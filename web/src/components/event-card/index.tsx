import {
  Calendar03Icon,
  Coins01Icon,
  FavouriteIcon,
  Location01Icon,
  UserGroupIcon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { Badge } from "~/components/ui/badge";
import { Button } from "~/components/ui/button";
import { Card, CardContent } from "~/components/ui/card";

interface EventCardProps {
  date: string;
  description: string;
  hasDonation: boolean;
  hasVolunteering: boolean;
  imageUrl: string;
  location: string;
  ongName: string;
  title: string;
  volunteersNeeded: number;
  volunteersRegistered: number;
}

export function EventCard({
  title,
  description,
  date,
  location,
  ongName,
  imageUrl,
  volunteersNeeded,
  volunteersRegistered,
  hasVolunteering,
  hasDonation,
}: EventCardProps) {
  const progress = Math.min(
    (volunteersRegistered / volunteersNeeded) * 100,
    100
  );

  return (
    <Card className="group overflow-hidden p-0 transition-all">
      <div className="flex flex-col md:flex-row">
        <div className="relative aspect-video w-full overflow-hidden md:aspect-square md:w-48 lg:w-56">
          {/** biome-ignore lint/correctness/useImageSize: Image comes from external source */}
          <img
            alt={title}
            className="h-full object-cover transition-transform duration-300 group-hover:scale-105"
            src={imageUrl}
          />
          <div className="absolute top-3 left-3 flex gap-2">
            {hasVolunteering && (
              <Badge className="bg-primary/90 text-primary-foreground backdrop-blur-sm">
                <HugeiconsIcon className="mr-1 size-3" icon={UserGroupIcon} />
                Voluntariado
              </Badge>
            )}
            {hasDonation && (
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
                <span>{date}</span>
              </div>
              <div className="flex items-center gap-1">
                <HugeiconsIcon className="size-4" icon={Location01Icon} />
                <span>{location}</span>
              </div>
            </div>
          </div>

          {hasVolunteering && (
            <div className="mt-4">
              <div className="mb-2 flex items-center justify-between text-sm">
                <span className="text-muted-foreground">Voluntários</span>
                <span className="font-medium">
                  {volunteersRegistered}/{volunteersNeeded}
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

          <div className="mt-4 flex gap-3">
            <Button className="flex-1">Participar</Button>
            <Button variant="outline">Detalhes</Button>
          </div>
        </CardContent>
      </div>
    </Card>
  );
}
