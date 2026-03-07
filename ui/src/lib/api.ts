import type {
  AdminSnapshot,
  Assignment,
  Capability,
  CurrentSession,
  DocumentTypeDefinition,
  InspectionDomain,
  InspectionTopic,
  Role,
  ScoreConfiguration
} from '../types';

type ApiError = {
  code?: string;
  message?: string;
};

export async function fetchAdminSnapshot(accessToken: string, signal: AbortSignal): Promise<AdminSnapshot> {
  const session = await fetchJson<CurrentSession>('/api/session', accessToken, signal);

  const [roles, capabilities, assignments, taxonomyPayload, documentTypes, scoreConfigurations] = await Promise.all([
    canManageUsers(session) ? fetchJson<Role[]>('/api/admin/roles', accessToken, signal) : Promise.resolve([]),
    canManageUsers(session) ? fetchJson<Capability[]>('/api/admin/capabilities', accessToken, signal) : Promise.resolve([]),
    canManageUsers(session) && session.userId != null
      ? fetchJson<Assignment[]>(`/api/admin/users/${session.userId}/assignments`, accessToken, signal)
      : Promise.resolve([]),
    canManageTaxonomy(session)
      ? fetchJson<{ domains: InspectionDomain[]; topics: InspectionTopic[] }>('/api/admin/inspection-domains', accessToken, signal)
      : Promise.resolve({ domains: [], topics: [] }),
    canManageTaxonomy(session)
      ? fetchJson<DocumentTypeDefinition[]>('/api/admin/document-types', accessToken, signal)
      : Promise.resolve([]),
    canManageScores(session)
      ? fetchJson<ScoreConfiguration[]>('/api/admin/score-configurations', accessToken, signal)
      : Promise.resolve([])
  ]);

  return {
    session,
    roles,
    capabilities,
    assignments,
    inspectionDomains: taxonomyPayload.domains,
    inspectionTopics: taxonomyPayload.topics,
    documentTypes,
    scoreConfigurations
  };
}

async function fetchJson<T>(path: string, accessToken: string, signal: AbortSignal): Promise<T> {
  const response = await fetch(path, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
      Accept: 'application/json'
    },
    signal
  });

  if (!response.ok) {
    let apiError: ApiError | null = null;
    try {
      apiError = await response.json() as ApiError;
    } catch {
      apiError = null;
    }
    throw new Error(apiError?.message ?? `Aanroep mislukt voor ${path} (${response.status}).`);
  }

  return response.json() as Promise<T>;
}

function canManageUsers(session: CurrentSession) {
  return session.capabilities.includes('MANAGE_USERS');
}

function canManageTaxonomy(session: CurrentSession) {
  return session.capabilities.includes('MANAGE_TAXONOMY');
}

function canManageScores(session: CurrentSession) {
  return session.capabilities.includes('MANAGE_SCORE_CONFIGURATION');
}
