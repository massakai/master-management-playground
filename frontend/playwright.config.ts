import { defineConfig, devices } from '@playwright/test'
import { execSync } from 'node:child_process'
import { fileURLToPath } from 'node:url'
import path from 'node:path'

const frontendPort = 4173
const backendPort = 8080
const frontendDir = path.dirname(fileURLToPath(import.meta.url))
const backendDir = path.resolve(frontendDir, '../backend')
const gradleUserHome = path.join(backendDir, '.gradle-playwright')
const backendJavaHome =
  process.env.JAVA_HOME ??
  (process.platform === 'darwin'
    ? execSync('/usr/libexec/java_home -v 25', { encoding: 'utf-8' }).trim()
    : undefined)

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: Boolean(process.env.CI),
  retries: process.env.CI ? 2 : 0,
  reporter: process.env.CI ? [['github'], ['html', { open: 'never' }]] : 'list',
  use: {
    baseURL: `http://127.0.0.1:${frontendPort}`,
    trace: 'on-first-retry'
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] }
    }
  ],
  webServer: [
    {
      command: './gradlew --no-daemon bootRun',
      cwd: backendDir,
      env: {
        ...process.env,
        ...(backendJavaHome ? { JAVA_HOME: backendJavaHome } : {}),
        GRADLE_USER_HOME: gradleUserHome
      },
      url: `http://127.0.0.1:${backendPort}/api/categories`,
      reuseExistingServer: !process.env.CI,
      timeout: 120 * 1000
    },
    {
      command: `npm run dev -- --host 127.0.0.1 --port ${frontendPort}`,
      cwd: frontendDir,
      url: `http://127.0.0.1:${frontendPort}`,
      reuseExistingServer: !process.env.CI,
      timeout: 120 * 1000
    }
  ]
})
