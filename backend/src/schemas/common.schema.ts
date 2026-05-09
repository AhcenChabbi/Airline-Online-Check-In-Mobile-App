import { z } from "zod";
import { extendZodWithOpenApi } from "@asteasolutions/zod-to-openapi";

extendZodWithOpenApi(z);

export const healthResponseSchema = z
  .object({
    message: z.string(),
  })
  .openapi("HealthResponse");
