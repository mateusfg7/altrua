import { useCallback } from "react";
import { NGOCard } from "~/components/ngo-card";
import { Skeleton } from "~/components/ui/skeleton";
import { useInfiniteNgoList } from "~/hooks/use-infinite-ngo-list";
import { LoadMoreTrigger } from "./load-more-trigger";

function NgoFeedSkeleton() {
  return (
    <div className="grid gap-6 sm:grid-cols-2">
      {Array.from({ length: 6 }).map((_, index) => (
        <Skeleton
          className="h-90 w-full rounded-xl bg-muted-foreground/20"
          // biome-ignore lint/suspicious/noArrayIndexKey: skeleton placeholders have no stable id
          key={index}
        />
      ))}
    </div>
  );
}

export function NgoFeed() {
  const {
    data: ngos,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteNgoList();

  const handleLoadMore = useCallback(() => {
    fetchNextPage();
  }, [fetchNextPage]);

  if (isLoading) {
    return <NgoFeedSkeleton />;
  }

  if (isError) {
    return (
      <p className="py-16 text-center text-muted-foreground">
        Não foi possível carregar as ONGs. Tente novamente mais tarde.
      </p>
    );
  }

  if (!ngos || ngos.length === 0) {
    return (
      <p className="py-16 text-center text-muted-foreground">
        Nenhuma ONG por aqui ainda.
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <div className="grid gap-6 sm:grid-cols-2">
        {ngos.map((ngo) => (
          <NGOCard data={ngo} key={ngo.id} />
        ))}

        {isFetchingNextPage &&
          Array.from({ length: 2 }).map((_, index) => (
            <Skeleton
              className="h-90 w-full rounded-xl bg-muted-foreground/20"
              // biome-ignore lint/suspicious/noArrayIndexKey: skeleton placeholders have no stable id
              key={index}
            />
          ))}
      </div>

      <LoadMoreTrigger
        enabled={hasNextPage && !isFetchingNextPage}
        onLoadMore={handleLoadMore}
      />

      {!hasNextPage && (
        <p className="py-4 text-center text-muted-foreground text-sm">
          Você chegou ao fim do feed.
        </p>
      )}
    </div>
  );
}
