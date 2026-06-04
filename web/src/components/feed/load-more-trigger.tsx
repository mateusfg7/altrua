import { useEffect, useRef } from "react";

type LoadMoreTriggerProps = {
  /** Whether more pages are available and we are not already fetching. */
  enabled: boolean;
  /** Called when the sentinel scrolls into view. */
  onLoadMore: () => void;
};

/**
 * Invisible sentinel that triggers `onLoadMore` once it enters the viewport.
 * Render it at the end of an infinite list to drive infinite scrolling.
 */
export function LoadMoreTrigger({ enabled, onLoadMore }: LoadMoreTriggerProps) {
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const node = ref.current;
    if (!(node && enabled)) {
      return;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0]?.isIntersecting) {
          onLoadMore();
        }
      },
      { rootMargin: "300px" }
    );

    observer.observe(node);
    return () => observer.disconnect();
  }, [enabled, onLoadMore]);

  return <div aria-hidden className="h-px w-full" ref={ref} />;
}
