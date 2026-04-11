import { ArrowRight01Icon } from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { Button } from "~/components/ui/button";
import { useNgoList } from "~/features/ngos/hooks/use-ngo-list";
import { NGOCard } from "../ngo-card";
import { NGOCardSkeleton } from "./ngo-card-skeleton";

export function NGOSection() {
  const { data, isLoading } = useNgoList({ page: 0, size: 6 });

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

          <div className="grid w-full 3xl:grid-cols-4 gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {isLoading && <NGOCardSkeleton />}

            {!isLoading &&
              data?.content.map((ngo) => <NGOCard data={ngo} key={ngo.id} />)}
          </div>
        </div>
      </div>
    </section>
  );
}
