import { Skeleton } from "../ui/skeleton";

export function NGOCardSkeleton() {
  return (
    <>
      {Array.from({ length: 6 }).map((_, index) => (
        <Skeleton
          className="h-90 w-full rounded-xl bg-muted-foreground/20"
          // biome-ignore lint/suspicious/noArrayIndexKey: Only used for skeleton loading state, no dynamic data involved
          key={index}
        />
      ))}
    </>
  );
}
