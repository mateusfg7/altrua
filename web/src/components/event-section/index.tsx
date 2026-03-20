// import { ArrowRight01Icon, FilterIcon } from "hugeicons-react";

import { ArrowRight01Icon, FilterIcon } from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { EventCard } from "@/components/event-card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

const mockEvents = [
  {
    id: 1,
    title: "Mutirão de Plantio no Parque Central",
    description:
      "Venha ajudar a plantar 500 mudas nativas e contribuir para a recuperação da área verde do parque.",
    date: "25 Mar, 2026 · 08:00",
    location: "Parque Central, São Paulo",
    ongName: "Verde Vida",
    imageUrl:
      "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=600&h=400&fit=crop",
    volunteersNeeded: 50,
    volunteersRegistered: 32,
    hasVolunteering: true,
    hasDonation: true,
  },
  {
    id: 2,
    title: "Feira de Adoção Animal",
    description:
      "Evento mensal de adoção responsável com mais de 30 cães e gatos esperando um novo lar.",
    date: "28 Mar, 2026 · 10:00",
    location: "Shopping Vila Olímpia, SP",
    ongName: "Patas Felizes",
    imageUrl:
      "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=600&h=400&fit=crop",
    volunteersNeeded: 20,
    volunteersRegistered: 18,
    hasVolunteering: true,
    hasDonation: false,
  },
  {
    id: 3,
    title: "Campanha do Agasalho 2026",
    description:
      "Arrecadação de roupas de inverno para famílias em situação de vulnerabilidade social.",
    date: "01 Abr - 30 Jun, 2026",
    location: "Diversos pontos, BH",
    ongName: "Mãos que Alimentam",
    imageUrl:
      "https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?w=600&h=400&fit=crop",
    volunteersNeeded: 100,
    volunteersRegistered: 45,
    hasVolunteering: true,
    hasDonation: true,
  },
  {
    id: 4,
    title: "Ação de Limpeza na Praia",
    description:
      "Junte-se a nós para limpar a praia e proteger a vida marinha. Todo material será fornecido.",
    date: "15 Abr, 2026 · 09:00",
    location: "Praia do Forte, Salvador",
    ongName: "Oceano Limpo",
    imageUrl:
      "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600&h=400&fit=crop",
    volunteersNeeded: 0,
    volunteersRegistered: 0,
    hasVolunteering: false,
    hasDonation: true,
  },
];

const filterCategories = [
  "Todos",
  "Voluntariado",
  "Doação",
  "Educação",
  "Animais",
  "Meio Ambiente",
];

export function EventsSection() {
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
          {mockEvents.map((event) => (
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
