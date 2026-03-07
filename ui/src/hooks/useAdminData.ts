import { startTransition, useEffect, useEffectEvent, useState } from 'react';
import { fetchAdminSnapshot } from '../lib/api';
import type { AdminSnapshot } from '../types';

type AdminDataState =
  | { status: 'idle'; snapshot: null }
  | { status: 'loading'; snapshot: AdminSnapshot | null }
  | { status: 'ready'; snapshot: AdminSnapshot }
  | { status: 'error'; snapshot: AdminSnapshot | null; message: string };

export function useAdminData(accessToken: string | null) {
  const [reloadCounter, setReloadCounter] = useState(0);
  const [state, setState] = useState<AdminDataState>({ status: 'idle', snapshot: null });

  const applySnapshot = useEffectEvent((snapshot: AdminSnapshot) => {
    startTransition(() => {
      setState({ status: 'ready', snapshot });
    });
  });

  const applyFailure = useEffectEvent((message: string, snapshot: AdminSnapshot | null) => {
    setState({ status: 'error', snapshot, message });
  });

  useEffect(() => {
    if (!accessToken) {
      setState({ status: 'idle', snapshot: null });
      return undefined;
    }

    const controller = new AbortController();
    setState((currentState) => ({ status: 'loading', snapshot: currentState.snapshot }));

    void fetchAdminSnapshot(accessToken, controller.signal)
      .then((snapshot) => {
        if (!controller.signal.aborted) {
          applySnapshot(snapshot);
        }
      })
      .catch((error: unknown) => {
        if (!controller.signal.aborted) {
          applyFailure(
            error instanceof Error ? error.message : 'De beheergegevens konden niet worden geladen.',
            null
          );
        }
      });

    return () => controller.abort();
  }, [accessToken, reloadCounter]);

  return {
    ...state,
    reload: () => setReloadCounter((count) => count + 1)
  };
}
