import { useForm } from "@tanstack/react-form";
import { Link, useRouter } from "@tanstack/react-router";
import { toast } from "sonner";
import z from "zod";
import { Button } from "~/components/ui/button";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "~/components/ui/field";
import { Input } from "~/components/ui/input";
import { useSignUp } from "~/hooks/use-sign-up";
import { cn } from "~/lib/utils";
import { useAuthStore } from "~/store/auth.store";

const schema = z.object({
  name: z
    .string("O nome é obrigatório")
    .min(2, "O nome deve ter pelo menos 2 caracteres")
    .max(100, "O nome não pode ter mais de 100 caracteres"),
  email: z.email("O email deve ser válido"),
  password: z
    .string("A senha é obrigatória")
    .min(6, "A senha deve ter pelo menos 6 caracteres")
    .max(100, "A senha não pode ter mais de 100 caracteres"),
});

export function SignUpForm() {
  const { setTokens } = useAuthStore();
  const { mutateAsync } = useSignUp();

  const router = useRouter();

  const form = useForm({
    defaultValues: {
      name: "",
      email: "",
      password: "",
    } as z.infer<typeof schema>,
    validators: {
      onSubmit: schema,
    },
    onSubmit: async ({ value }) => {
      await mutateAsync(value, {
        onSuccess: (tokens) => {
          toast.success("Conta criada com sucesso! Redirecionando...");
          setTokens(tokens);
          router.navigate({ to: "/" });
        },
        onError: (error) => {
          toast.error(
            error.response?.data?.detail || "Ocorreu um erro ao criar a conta"
          );
        },
      });
    },
  });

  return (
    <form
      className={cn("flex flex-col gap-6")}
      onSubmit={(e) => {
        e.preventDefault();
        form.handleSubmit();
      }}
    >
      <div className="flex flex-col items-center gap-1 text-center">
        <h1 className="font-bold text-2xl">Crie sua conta</h1>
        <p className="text-balance text-muted-foreground text-sm">
          Preencha os campos abaixo para criar sua conta
        </p>
      </div>

      <FieldGroup>
        {/* Name */}
        <form.Field name="name">
          {(field) => {
            const isInvalid =
              field.state.meta.isTouched && !field.state.meta.isValid;
            return (
              <Field data-invalid={isInvalid}>
                <FieldLabel htmlFor={field.name}>Nome completo</FieldLabel>
                <Input
                  aria-invalid={isInvalid}
                  autoComplete="name"
                  id={field.name}
                  name={field.name}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                  placeholder="Seu nome completo"
                  value={field.state.value}
                />
                {isInvalid && <FieldError errors={field.state.meta.errors} />}
              </Field>
            );
          }}
        </form.Field>

        {/* Email */}
        <form.Field name="email">
          {(field) => {
            const isInvalid =
              field.state.meta.isTouched && !field.state.meta.isValid;
            return (
              <Field data-invalid={isInvalid}>
                <FieldLabel htmlFor={field.name}>Email</FieldLabel>
                <Input
                  aria-invalid={isInvalid}
                  autoComplete="email"
                  id={field.name}
                  name={field.name}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                  placeholder="eu@exemplo.com"
                  type="email"
                  value={field.state.value}
                />
                {isInvalid && <FieldError errors={field.state.meta.errors} />}
              </Field>
            );
          }}
        </form.Field>

        {/* Password */}
        <form.Field name="password">
          {(field) => {
            const isInvalid =
              field.state.meta.isTouched && !field.state.meta.isValid;
            return (
              <Field data-invalid={isInvalid}>
                <FieldLabel htmlFor={field.name}>Senha</FieldLabel>
                <Input
                  aria-invalid={isInvalid}
                  autoComplete="new-password"
                  id={field.name}
                  name={field.name}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                  type="password"
                  value={field.state.value}
                />
                {isInvalid && <FieldError errors={field.state.meta.errors} />}
              </Field>
            );
          }}
        </form.Field>

        {/* Submit */}
        <Field>
          <form.Subscribe selector={(s) => s.isSubmitting}>
            {(isSubmitting) => (
              <Button className="w-full" disabled={isSubmitting} type="submit">
                {isSubmitting ? "Cadastrando..." : "Cadastrar"}
              </Button>
            )}
          </form.Subscribe>
          <p className="text-center text-sm">
            Já tem uma conta?{" "}
            <Button asChild className="px-0" variant="link">
              <Link to="/sign-in">Faça login</Link>
            </Button>
          </p>
        </Field>
      </FieldGroup>
    </form>
  );
}
