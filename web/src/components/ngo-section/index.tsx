import { ArrowRight01Icon } from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { NGOCard } from "~/components/ngo-card";
import { Button } from "~/components/ui/button";

const mockONGs = [
  {
    id: 1,
    name: "Instituto Esperança Viva",
    description:
      "Promovemos educação e capacitação profissional para jovens em situação de vulnerabilidade social.",
    area: "Educação",
    location: "São Paulo, SP",
    imageUrl:
      "https://images.unsplash.com/photo-1509062522246-3755977927d7?w=600&h=400&fit=crop",
    eventsCount: 5,
  },
  {
    id: 2,
    name: "Patas Felizes",
    description:
      "Resgatamos, cuidamos e promovemos a adoção responsável de animais abandonados.",
    area: "Animais",
    location: "Rio de Janeiro, RJ",
    imageUrl:
      "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=600&h=400&fit=crop",
    eventsCount: 3,
  },
  {
    id: 3,
    name: "Mãos que Alimentam",
    description:
      "Distribuímos refeições nutritivas para pessoas em situação de rua e famílias carentes.",
    area: "Alimentação",
    location: "Belo Horizonte, MG",
    imageUrl:
      "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?w=600&h=400&fit=crop",
    eventsCount: 8,
  },
  {
    id: 4,
    name: "Verde Vida",
    description:
      "Trabalhamos pela preservação ambiental através de reflorestamento e educação ecológica.",
    area: "Meio Ambiente",
    location: "Curitiba, PR",
    imageUrl:
      "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=600&h=400&fit=crop",
    eventsCount: 4,
  },
];

export function NGOSection() {
  return (
    <section className="bg-muted px-3 py-16" id="ongs">
      <div className="mx-auto max-w-6xl">
        <div>
          <h2 className="font-bold text-3xl tracking-tight md:text-4xl">
            ONGs em Destaque
          </h2>
          <p className="mt-2 max-w-xl text-pretty text-muted-foreground">
            Conheça organizações que estão transformando comunidades e descubra
            como você pode contribuir.
          </p>
        </div>

        <div className="flex flex-col items-end gap-3">
          <Button className="w-fit" variant="ghost">
            Ver todas as ONGs
            <HugeiconsIcon icon={ArrowRight01Icon} />
          </Button>
          <div className="grid 3xl:grid-cols-4 gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {mockONGs.map((ong) => (
              <NGOCard key={ong.id} {...ong} />
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}
