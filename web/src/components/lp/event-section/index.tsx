import { ArrowRight01Icon, FilterIcon } from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { useState } from "react";
import { EventCard } from "~/components/lp/event-card";
import { Badge } from "~/components/ui/badge";
import { Button } from "~/components/ui/button";
import { useNgoEventList } from "~/hooks/use-ngo-event-list";
import { useNgoEventTagList } from "~/hooks/use-ngo-event-tag-list";
import { cn } from "~/lib/utils";

export function EventsSection() {
  const [listLength, setLength] = useState(10)

  const [tag, setTag] = useState<undefined | string>();

  const { data: tags } = useNgoEventTagList();
  const { data: events, isFetching } = useNgoEventList({ tag, size: listLength });

  function toggleTag(newTag: string) {
    if (newTag === tag) {
      setTag(undefined);
    } else {
      setTag(newTag);
    }
  }

  return (
    <section className="px-3 py-16" id="eventos">
      <div className="mx-auto max-w-6xl">
        <div className="mb-8 flex flex-col items-start justify-between gap-4 md:flex-row md:items-end">
          <div>
            <h2 className="font-bold text-3xl tracking-tight md:text-4xl">
              Próximos Eventos
            </h2>
            <p className="mt-2 max-w-xl text-muted-foreground">
              Encontre oportunidades de voluntariado e campanhas de doação que
              estão acontecendo perto de você.
            </p>
          </div>
          <Button className="gap-2" variant="ghost">
            Ver todos os eventos
            <HugeiconsIcon className="size-4" icon={ArrowRight01Icon} />
          </Button>
        </div>

        <div className="mb-8 flex flex-wrap items-center gap-2">
          <div className="flex items-center gap-2 text-muted-foreground text-sm">
            <HugeiconsIcon className="size-4" icon={FilterIcon} />
            <span>Filtrar</span>
          </div>
          {tags?.map((currTag) => (
            <Badge
              asChild
              className="cursor-pointer transition-colors hover:bg-primary hover:text-primary-foreground"
              key={currTag.id}
              variant={currTag.name === tag ? "default" : "secondary"}
            >
              <Button onClick={() => toggleTag(currTag.name)}>
                {currTag.name}
              </Button>
            </Badge>
          ))}
        </div>

        <div className={cn("grid gap-6", isFetching && "opacity-50")}>
          {events?.content.map((event) => (
            <EventCard key={event.id} {...event} />
          ))}
        </div>

        <div className="mt-10 text-center">
          <Button className="gap-2" size="lg" variant="outline" onClick={() => setLength(prev => prev + 5)}>
            Carregar mais eventos
            <HugeiconsIcon className="size-4" icon={ArrowRight01Icon} />
          </Button>
        </div>
      </div>
    </section>
  );
}
