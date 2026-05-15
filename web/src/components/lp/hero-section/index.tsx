import {
  ArrowRight01Icon,
  Calendar03Icon,
  FavouriteIcon,
  UserGroupIcon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { Button } from "~/components/ui/button";

export function HeroSection() {
  return (
    <section className="px-3">
      <div className="mx-auto max-w-6xl">
        <div className="mx-auto max-w-3xl text-center">
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-border bg-card px-4 py-1.5 font-medium text-muted-foreground text-sm">
            <HugeiconsIcon
              className="h-4 w-4 text-primary"
              icon={FavouriteIcon}
            />
            <span>Faça a diferença na sua comunidade</span>
          </div>

          <h1 className="text-balance font-bold text-4xl tracking-tight md:text-5xl lg:text-6xl">
            Conectando quem quer <span className="text-primary">ajudar</span>{" "}
            com quem <span className="text-primary">precisa</span>
          </h1>

          <p className="mx-auto mt-6 max-w-2xl text-pretty text-lg text-muted-foreground md:text-xl">
            O Altrua é a plataforma que une ONGs, voluntários e doadores.
            Encontre causas que importam para você e contribua com seu tempo ou
            recursos.
          </p>

          <div className="mt-10 flex flex-col items-center justify-center gap-4 sm:flex-row">
            <Button className="gap-2" size="lg">
              Explorar ONGs
              <HugeiconsIcon className="h-4 w-4" icon={ArrowRight01Icon} />
            </Button>
            <Button size="lg" variant="outline">
              Ver Eventos
            </Button>
          </div>
        </div>

        <div className="mx-auto mt-16 grid max-w-4xl grid-cols-1 gap-4 sm:grid-cols-3">
          <div className="flex flex-col items-center gap-2 rounded-xl border border-border bg-card p-6 text-center">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
              <HugeiconsIcon
                className="h-6 w-6 text-primary"
                icon={FavouriteIcon}
              />
            </div>
            <span className="font-bold text-2xl">150+</span>
            <span className="text-muted-foreground text-sm">
              ONGs cadastradas
            </span>
          </div>

          <div className="flex flex-col items-center gap-2 rounded-xl border border-border bg-card p-6 text-center">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
              <HugeiconsIcon
                className="h-6 w-6 text-primary"
                icon={UserGroupIcon}
              />
            </div>
            <span className="font-bold text-2xl">2.500+</span>
            <span className="text-muted-foreground text-sm">
              Voluntários ativos
            </span>
          </div>

          <div className="flex flex-col items-center gap-2 rounded-xl border border-border bg-card p-6 text-center">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
              <HugeiconsIcon
                className="h-6 w-6 text-primary"
                icon={Calendar03Icon}
              />
            </div>
            <span className="font-bold text-2xl">80+</span>
            <span className="text-muted-foreground text-sm">
              Eventos este mês
            </span>
          </div>
        </div>
      </div>
    </section>
  );
}
