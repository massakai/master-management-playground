#!/usr/bin/env python3

from __future__ import annotations

import sys
from pathlib import Path

try:
    import yaml
except ImportError as exc:  # pragma: no cover - import guard
    raise SystemExit(
        "PyYAML is required. Run `uv sync --group dev` to install dependencies."
    ) from exc

HTTP_METHODS = {"get", "post", "put", "patch", "delete", "options", "head", "trace"}
SPEC_PATH = Path(__file__).resolve().parent.parent / "docs" / "openapi.yaml"


class OpenApiValidator:
    def __init__(self, spec: dict):
        self.spec = spec
        self.errors: list[str] = []

    def validate(self) -> list[str]:
        self._validate_root()
        self._validate_paths()
        self._validate_components()
        self._validate_refs()
        self._validate_operation_ids()
        return self.errors

    def _validate_root(self) -> None:
        self._require_key(self.spec, "openapi", "root")
        self._require_key(self.spec, "info", "root")
        self._require_key(self.spec, "paths", "root")

        info = self.spec.get("info")
        if isinstance(info, dict):
            self._require_key(info, "title", "info")
            self._require_key(info, "version", "info")

    def _validate_paths(self) -> None:
        paths = self.spec.get("paths")
        if not isinstance(paths, dict) or not paths:
            self.errors.append("paths must be a non-empty object")
            return

        for path, path_item in paths.items():
            if not isinstance(path, str) or not path.startswith("/"):
                self.errors.append(f"path {path!r} must start with /")

            if not isinstance(path_item, dict):
                self.errors.append(f"path {path!r} must map to an object")
                continue

            operations = {
                method: operation
                for method, operation in path_item.items()
                if method in HTTP_METHODS
            }
            if not operations:
                self.errors.append(f"path {path!r} must define at least one HTTP operation")

            for method, operation in operations.items():
                self._validate_operation(path, method, operation)

    def _validate_operation(self, path: str, method: str, operation: object) -> None:
        if not isinstance(operation, dict):
            self.errors.append(f"operation {method.upper()} {path} must be an object")
            return

        self._require_key(operation, "responses", f"{method.upper()} {path}")
        responses = operation.get("responses")
        if not isinstance(responses, dict) or not responses:
            self.errors.append(f"operation {method.upper()} {path} must define responses")

    def _validate_components(self) -> None:
        components = self.spec.get("components")
        if components is None:
            return

        if not isinstance(components, dict):
            self.errors.append("components must be an object")
            return

        schemas = components.get("schemas")
        if schemas is not None and not isinstance(schemas, dict):
            self.errors.append("components.schemas must be an object")

    def _validate_refs(self) -> None:
        for obj in self._walk(self.spec):
            if not isinstance(obj, dict) or "$ref" not in obj:
                continue

            ref = obj["$ref"]
            if not isinstance(ref, str) or not ref.startswith("#/"):
                self.errors.append(f"only internal refs are supported in this validator: {ref!r}")
                continue

            if self._resolve_pointer(ref) is None:
                self.errors.append(f"unresolved ref: {ref}")

    def _validate_operation_ids(self) -> None:
        operation_ids: list[str] = []
        for path_item in self.spec.get("paths", {}).values():
            if not isinstance(path_item, dict):
                continue
            for method, operation in path_item.items():
                if method not in HTTP_METHODS or not isinstance(operation, dict):
                    continue
                operation_id = operation.get("operationId")
                if operation_id:
                    operation_ids.append(operation_id)

        seen: set[str] = set()
        duplicates: set[str] = set()
        for operation_id in operation_ids:
            if operation_id in seen:
                duplicates.add(operation_id)
            seen.add(operation_id)

        for operation_id in sorted(duplicates):
            self.errors.append(f"duplicate operationId: {operation_id}")

    def _resolve_pointer(self, ref: str):
        current = self.spec
        parts = ref.removeprefix("#/").split("/")
        for part in parts:
            part = part.replace("~1", "/").replace("~0", "~")
            if isinstance(current, list):
                try:
                    index = int(part)
                except ValueError:
                    return None
                if index >= len(current):
                    return None
                current = current[index]
            elif isinstance(current, dict):
                if part not in current:
                    return None
                current = current[part]
            else:
                return None
        return current

    def _walk(self, node):
        yield node
        if isinstance(node, dict):
            for value in node.values():
                yield from self._walk(value)
        elif isinstance(node, list):
            for value in node:
                yield from self._walk(value)

    def _require_key(self, obj: object, key: str, context: str) -> None:
        if not isinstance(obj, dict) or key not in obj:
            self.errors.append(f"missing required key {key!r} in {context}")


def main() -> int:
    with SPEC_PATH.open("r", encoding="utf-8") as file:
        spec = yaml.safe_load(file)

    errors = OpenApiValidator(spec).validate()
    if not errors:
        print(f"PASS: {SPEC_PATH}")
        print(
            "Checked: YAML parse, required OpenAPI root fields, paths/operations, "
            "internal refs, operationId uniqueness"
        )
        return 0

    print(f"FAIL: {SPEC_PATH}", file=sys.stderr)
    for error in errors:
        print(f"- {error}", file=sys.stderr)
    return 1


if __name__ == "__main__":
    raise SystemExit(main())
