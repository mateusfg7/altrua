import { useCallback } from "react";
import { EventCard } from "~/components/lp/event-card";
import { Skeleton } from "~/components/ui/skeleton";
import { useInfiniteNgoEventList } from "~/hooks/use-infinite-ngo-event-list";
import { LoadMoreTrigger } from "./load-more-trigger";

function EventFeedSkeleton() {
  return (
    <div className="grid gap-6">
      {Array.from({ length: 4 }).map((_, index) => (
        <Skeleton
          className="h-48 w-full rounded-xl bg-muted-foreground/20"
          // biome-ignore lint/suspicious/noArrayIndexKey: skeleton placeholders have no stable id
          key={index}
        />
      ))}
    </div>
  );
}

export function EventFeed() {
  const {
    data: events,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteNgoEventList();

  const handleLoadMore = useCallback(() => {
    fetchNextPage();
  }, [fetchNextPage]);

  if (isLoading) {
    return <EventFeedSkeleton />;
  }

  if (isError) {
    return (
      <p className="py-16 text-center text-muted-foreground">
        Não foi possível carregar os eventos. Tente novamente mais tarde.
      </p>
    );
  }

  if (!events || events.length === 0) {
    return (
      <p className="py-16 text-center text-muted-foreground">
        Nenhum evento por aqui ainda.
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <div className="grid gap-6">
        {events.map((event) => (
          <EventCard key={event.id} {...event} />
        ))}

        {isFetchingNextPage && (
          <Skeleton className="h-48 w-full rounded-xl bg-muted-foreground/20" />
        )}
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
