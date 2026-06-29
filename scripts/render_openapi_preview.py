#!/usr/bin/env python3

from __future__ import annotations

from pathlib import Path

try:
    import yaml
except ImportError as exc:  # pragma: no cover - import guard
    raise SystemExit(
        "PyYAML is required. Run `uv sync --group dev` to install dependencies."
    ) from exc

try:
    from jinja2 import Environment, FileSystemLoader, select_autoescape
except ImportError as exc:  # pragma: no cover - import guard
    raise SystemExit(
        "Jinja2 is required. Run `uv sync --group dev` to install dependencies."
    ) from exc

HTTP_METHODS = ["get", "post", "put", "patch", "delete", "options", "head", "trace"]
ROOT = Path(__file__).resolve().parent.parent
SPEC_PATH = ROOT / "docs" / "openapi.yaml"
OUTPUT_PATH = ROOT / "docs" / "openapi-preview.html"
TEMPLATE_DIR = ROOT / "templates"


def build_context(spec: dict) -> dict:
    info = spec.get("info", {})
    endpoints = []
    for path, path_item in spec.get("paths", {}).items():
        if not isinstance(path_item, dict):
            continue
        for method in HTTP_METHODS:
            operation = path_item.get(method)
            if not isinstance(operation, dict):
                continue
            endpoints.append(
                {
                    "method": method,
                    "path": path,
                    "summary": operation.get("summary", ""),
                    "description": operation.get("description", ""),
                    "operation_id": operation.get("operationId", ""),
                    "request_body": list(
                        operation.get("requestBody", {}).get("content", {}).keys()
                    ),
                    "responses": [
                        {
                            "status": status,
                            "description": response.get("description", "")
                            if isinstance(response, dict)
                            else "",
                        }
                        for status, response in operation.get("responses", {}).items()
                    ],
                }
            )

    schemas = []
    for schema_name, schema in spec.get("components", {}).get("schemas", {}).items():
        properties = []
        for property_name, prop in schema.get("properties", {}).items():
            prop_type = prop.get("type") or prop.get("format") or prop.get("$ref") or "-"
            properties.append({"name": property_name, "type": prop_type})
        schemas.append(
            {
                "name": schema_name,
                "type": schema.get("type", "-"),
                "properties": properties,
            }
        )

    return {
        "title": info.get("title", "OpenAPI Preview"),
        "description": info.get("description", ""),
        "openapi_version": spec.get("openapi", ""),
        "api_version": info.get("version", ""),
        "endpoints": endpoints,
        "schemas": schemas,
    }


def main() -> int:
    with SPEC_PATH.open("r", encoding="utf-8") as file:
        spec = yaml.safe_load(file)

    env = Environment(
        loader=FileSystemLoader(TEMPLATE_DIR),
        autoescape=select_autoescape(["html", "xml"]),
    )
    template = env.get_template("openapi_preview.html.j2")
    html = template.render(**build_context(spec))

    OUTPUT_PATH.write_text(html, encoding="utf-8")
    print(f"Generated {OUTPUT_PATH}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
