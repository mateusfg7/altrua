import { ArrowRight01Icon, FilterIcon } from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { useNGOEventList } from "~/hooks/use-ngo-event-list";
import { EventCard } from "~/shared/components/lp/event-card";
import { Badge } from "~/shared/components/ui/badge";
import { Button } from "~/shared/components/ui/button";

const filterCategories = [
  "Todos",
  "Voluntariado",
  "Doação",
  "Educação",
  "Animais",
  "Meio Ambiente",
];

export function EventsSection() {
  const { data } = useNGOEventList();

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
            <span>Filtrar:</span>
          </div>
          {filterCategories.map((category, index) => (
            <Badge
              className="cursor-pointer transition-colors hover:bg-primary hover:text-primary-foreground"
              key={category}
              variant={index === 0 ? "default" : "secondary"}
            >
              {category}
            </Badge>
          ))}
        </div>

        <div className="grid gap-6">
          {data?.content.map((event) => (
            <EventCard key={event.id} {...event} />
          ))}
        </div>

        <div className="mt-10 text-center">
          <Button className="gap-2" size="lg" variant="outline">
            Carregar mais eventos
            <HugeiconsIcon className="size-4" icon={ArrowRight01Icon} />
          </Button>
        </div>
      </div>
    </section>
  );
}
