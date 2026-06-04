import {
  Calendar03Icon,
  FavouriteIcon,
  Logout01Icon,
  PlusSignIcon,
  UserGroupIcon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { Link } from "@tanstack/react-router";
import { Avatar, AvatarImage } from "~/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "~/components/ui/dropdown-menu";
import { useProfile } from "~/hooks/use-profile";
import { cn } from "~/lib/utils";
import { useAuthStore } from "~/store/auth.store";
import { useDialogStore } from "~/store/dialog.store";
import { type FeedSection, useFeedStore } from "~/store/feed.store";

const navItems: { section: FeedSection; label: string; icon: typeof Calendar03Icon }[] =
  [
    { section: "eventos", label: "Eventos", icon: Calendar03Icon },
    { section: "ongs", label: "ONGs", icon: UserGroupIcon },
  ];

export function AppSidebar() {
  const section = useFeedStore((s) => s.section);
  const setSection = useFeedStore((s) => s.setSection);

  const clearTokens = useAuthStore((s) => s.clearTokens);
  const setDialog = useDialogStore((s) => s.setDialog);

  const { data: profile, refetch } = useProfile();

  return (
    <aside className="sticky top-0 flex h-dvh w-16 shrink-0 flex-col gap-6 border-sidebar-border border-r bg-sidebar p-3 text-sidebar-foreground md:w-64">
      <Link className="flex items-center gap-2 px-1 py-2" to="/">
        <div className="flex size-9 shrink-0 items-center justify-center rounded-lg bg-primary">
          <HugeiconsIcon
            className="size-5 text-primary-foreground"
            icon={FavouriteIcon}
          />
        </div>
        <span className="hidden font-bold text-xl tracking-tight md:inline">
          Altrua
        </span>
      </Link>

      <nav className="flex flex-1 flex-col gap-1">
        {navItems.map((item) => {
          const active = section === item.section;

          return (
            <button
              className={cn(
                "flex items-center gap-3 rounded-lg px-3 py-2 font-medium text-sm transition-colors",
                "max-md:justify-center",
                active
                  ? "bg-sidebar-accent text-sidebar-accent-foreground"
                  : "text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground"
              )}
              key={item.section}
              onClick={() => setSection(item.section)}
              title={item.label}
              type="button"
            >
              <HugeiconsIcon className="size-5 shrink-0" icon={item.icon} />
              <span className="hidden md:inline">{item.label}</span>
            </button>
          );
        })}

        <button
          className="mt-2 flex items-center gap-3 rounded-lg border border-sidebar-border border-dashed px-3 py-2 font-medium text-sidebar-foreground/70 text-sm transition-colors hover:bg-sidebar-accent/50 hover:text-sidebar-foreground max-md:justify-center"
          onClick={() => setDialog("create-ngo")}
          title="Cadastrar ONG"
          type="button"
        >
          <HugeiconsIcon className="size-5 shrink-0" icon={PlusSignIcon} />
          <span className="hidden md:inline">Cadastrar ONG</span>
        </button>
      </nav>

      {profile && (
        <DropdownMenu>
          <DropdownMenuTrigger className="flex items-center gap-3 rounded-lg p-2 text-left outline-none transition-colors hover:bg-sidebar-accent/50 max-md:justify-center">
            <Avatar className="size-9 shrink-0">
              <AvatarImage
                src={`https://api.dicebear.com/10.x/initial-face/svg?eyesVariant=variant04,variant05,variant06,variant07,variant08&backgroundColor=ffa3b5,ffb382,ffe08a,e2f299,a8f0b0,86eadf,7fd4f5,cfbeff,b6e3f4&seed=${profile.name}`}
              />
            </Avatar>
            <div className="hidden min-w-0 flex-1 md:block">
              <p className="truncate font-medium text-sm">{profile.name}</p>
              <p className="truncate text-muted-foreground text-xs">
                {profile.email}
              </p>
            </div>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56" side="top">
            <DropdownMenuLabel>Minha conta</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuGroup>
              <DropdownMenuItem onClick={() => setDialog("create-ngo")}>
                <HugeiconsIcon icon={PlusSignIcon} />
                Cadastrar ONG
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => {
                  clearTokens();
                  refetch();
                }}
                variant="destructive"
              >
                <HugeiconsIcon icon={Logout01Icon} />
                Sair
              </DropdownMenuItem>
            </DropdownMenuGroup>
          </DropdownMenuContent>
        </DropdownMenu>
      )}
    </aside>
  );
}
