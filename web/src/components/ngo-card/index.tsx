import { LinkSquare02Icon, Location01Icon } from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { Badge } from "~/components/ui/badge";
import { Button } from "~/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from "~/components/ui/card";

type Props = {
  area: string;
  description: string;
  eventsCount: number;
  imageUrl: string;
  location: string;
  name: string;
};

export function NGOCard({
  name,
  description,
  area,
  location,
  imageUrl,
  eventsCount,
}: Props) {
  return (
    <Card className="group flex h-full flex-col overflow-hidden p-0 transition-all">
      <CardHeader className="p-0">
        <div className="relative aspect-video overflow-hidden">
          {/** biome-ignore lint/correctness/useImageSize: Image comes from external source */}
          <img
            alt={name}
            className="object-cover transition-transform duration-300 group-hover:scale-105"
            src={imageUrl}
          />
          <div className="absolute top-3 right-3">
            <Badge className="bg-card/90 backdrop-blur-sm" variant="secondary">
              {area}
            </Badge>
          </div>
        </div>
      </CardHeader>

      <CardContent className="flex flex-1 flex-col gap-3">
        <h3 className="line-clamp-1 font-semibold text-lg">{name}</h3>
        <p className="line-clamp-2 flex-1 text-muted-foreground text-sm">
          {description}
        </p>

        <div className="flex items-center gap-4 text-muted-foreground text-sm">
          <div className="flex items-center gap-1">
            <HugeiconsIcon className="size-4" icon={Location01Icon} />
            <span>{location}</span>
          </div>
          <span className="font-medium text-primary">
            {eventsCount} eventos
          </span>
        </div>
      </CardContent>

      <CardFooter className="p-5 pt-0">
        <Button className="w-full gap-2" variant="outline">
          Ver perfil
          <HugeiconsIcon className="size-4" icon={LinkSquare02Icon} />
        </Button>
      </CardFooter>
    </Card>
  );
}
