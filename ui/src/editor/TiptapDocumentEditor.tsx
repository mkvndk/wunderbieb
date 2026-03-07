import { useEffect, useMemo, useState } from "react";
import { EditorContent, useEditor } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import { DOCUMENT_TYPE_PROFILES } from "./documentTypeProfiles";

type Props = {
  accessToken: string;
  capabilities: string[];
};

type EditorDocumentVersion = {
  versionNumber: number;
  status: string;
  contentJson: string;
};

type EditorDocument = {
  id: number;
  documentTypeCode: string;
  title: string;
  activeVersionNumber: number;
  workflowStatus: string;
  versions: EditorDocumentVersion[];
};

export function TiptapDocumentEditor({ accessToken, capabilities }: Props) {
  const [selectedTypeCode, setSelectedTypeCode] = useState(DOCUMENT_TYPE_PROFILES[0].code);
  const [documentTitle, setDocumentTitle] = useState("Nieuw document");
  const [documentId, setDocumentId] = useState<number | null>(null);
  const [activeVersionNumber, setActiveVersionNumber] = useState<number | null>(null);
  const [workflowStatus, setWorkflowStatus] = useState<string>("DRAFT");
  const [statusMessage, setStatusMessage] = useState<string>("Nog niet opgeslagen.");
  const [isBusy, setIsBusy] = useState(false);

  const selectedProfile = useMemo(
    () => DOCUMENT_TYPE_PROFILES.find((profile) => profile.code === selectedTypeCode) ?? DOCUMENT_TYPE_PROFILES[0],
    [selectedTypeCode]
  );

  const canEdit = capabilities.includes("EDIT_DOCUMENT");
  const canReview = capabilities.includes("REVIEW_DOCUMENT");
  const canApprove = capabilities.includes("APPROVE_DOCUMENT");

  const editor = useEditor({
    extensions: [StarterKit],
    content: selectedProfile.templateHtml,
    editorProps: {
      attributes: {
        class: "tiptap-editor-content",
        "aria-label": "Documenteditor"
      }
    }
  });

  useEffect(() => {
    if (!editor) {
      return;
    }
    editor.commands.setContent(selectedProfile.templateHtml, true);
    setDocumentTitle(`${selectedProfile.title} ${new Date().toLocaleDateString("nl-NL")}`);
    setDocumentId(null);
    setActiveVersionNumber(null);
    setWorkflowStatus("DRAFT");
    setStatusMessage(`Template geladen voor ${selectedProfile.title}.`);
  }, [editor, selectedProfile]);

  if (!editor) {
    return (
      <section className="panel" aria-live="polite">
        <h2>Editor initialiseren</h2>
        <p>TipTap editor wordt gestart.</p>
      </section>
    );
  }

  const saveDraft = async () => {
    if (!canEdit) {
      setStatusMessage("Je mist EDIT_DOCUMENT rechten.");
      return;
    }
    setIsBusy(true);
    try {
      const contentJson = JSON.stringify(editor.getJSON());
      if (documentId == null) {
        const created = await callApi<EditorDocument>("/api/docs/documents", "POST", accessToken, {
          documentTypeCode: selectedTypeCode,
          title: documentTitle,
          contentJson
        });
        setDocumentId(created.id);
        setActiveVersionNumber(created.activeVersionNumber);
        setWorkflowStatus(created.workflowStatus);
        setStatusMessage(`Concept opgeslagen als document #${created.id}, versie ${created.activeVersionNumber}.`);
      } else {
        const updated = await callApi<EditorDocument>(`/api/docs/documents/${documentId}/versions`, "POST", accessToken, {
          contentJson,
          changeSummary: "Aanpassing vanuit TipTap editor"
        });
        setActiveVersionNumber(updated.activeVersionNumber);
        setWorkflowStatus(updated.workflowStatus);
        setStatusMessage(`Nieuwe conceptversie ${updated.activeVersionNumber} opgeslagen.`);
      }
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : "Opslaan mislukt.");
    } finally {
      setIsBusy(false);
    }
  };

  const submitForReview = async () => {
    if (!canReview || documentId == null || activeVersionNumber == null) {
      setStatusMessage("Review niet mogelijk: document of rechten ontbreken.");
      return;
    }
    setIsBusy(true);
    try {
      const updated = await callApi<EditorDocument>(`/api/docs/documents/${documentId}/review`, "POST", accessToken, {
        versionNumber: activeVersionNumber
      });
      setWorkflowStatus(updated.workflowStatus);
      setStatusMessage(`Versie ${activeVersionNumber} staat nu op IN_REVIEW.`);
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : "Reviewactie mislukt.");
    } finally {
      setIsBusy(false);
    }
  };

  const approve = async () => {
    if (!canApprove || documentId == null || activeVersionNumber == null) {
      setStatusMessage("Goedkeuren niet mogelijk: document of rechten ontbreken.");
      return;
    }
    setIsBusy(true);
    try {
      const updated = await callApi<EditorDocument>(`/api/docs/documents/${documentId}/approve`, "POST", accessToken, {
        versionNumber: activeVersionNumber
      });
      setWorkflowStatus(updated.workflowStatus);
      setStatusMessage(`Versie ${activeVersionNumber} is goedgekeurd.`);
    } catch (error) {
      setStatusMessage(error instanceof Error ? error.message : "Goedkeuren mislukt.");
    } finally {
      setIsBusy(false);
    }
  };

  return (
    <section aria-labelledby="editor-titel" className="panel">
      <div className="panel-header">
        <h2 id="editor-titel">Documenteditor (TipTap)</h2>
      </div>

      <div className="editor-meta">
        <label htmlFor="document-type-select">Documenttype</label>
        <select
          id="document-type-select"
          value={selectedTypeCode}
          onChange={(event) => setSelectedTypeCode(event.target.value)}
        >
          {DOCUMENT_TYPE_PROFILES.map((profile) => (
            <option key={profile.code} value={profile.code}>
              {profile.title}
            </option>
          ))}
        </select>

        <label htmlFor="document-title-input">Titel</label>
        <input
          id="document-title-input"
          className="editor-title-input"
          value={documentTitle}
          onChange={(event) => setDocumentTitle(event.target.value)}
          disabled={!canEdit || isBusy}
        />

        <p className="muted">{selectedProfile.summary}</p>
        <p className="muted">
          Document: {documentId ?? "-"} | Actieve versie: {activeVersionNumber ?? "-"} | Workflow: {workflowStatus}
        </p>
      </div>

      <div className="editor-toolbar" role="toolbar" aria-label="Editor opmaak">
        <button type="button" onClick={() => editor.chain().focus().toggleBold().run()} aria-pressed={editor.isActive("bold")} disabled={!canEdit || isBusy}>
          Vet
        </button>
        <button type="button" onClick={() => editor.chain().focus().toggleItalic().run()} aria-pressed={editor.isActive("italic")} disabled={!canEdit || isBusy}>
          Cursief
        </button>
        <button type="button" onClick={() => editor.chain().focus().toggleHeading({ level: 2 }).run()} aria-pressed={editor.isActive("heading", { level: 2 })} disabled={!canEdit || isBusy}>
          Kop 2
        </button>
        <button type="button" onClick={() => editor.chain().focus().toggleBulletList().run()} aria-pressed={editor.isActive("bulletList")} disabled={!canEdit || isBusy}>
          Lijst
        </button>
        <button type="button" onClick={() => editor.chain().focus().undo().run()} disabled={!canEdit || isBusy}>
          Ongedaan
        </button>
        <button type="button" onClick={() => editor.chain().focus().redo().run()} disabled={!canEdit || isBusy}>
          Opnieuw
        </button>
      </div>

      <EditorContent editor={editor} />

      <div className="editor-actions">
        <button type="button" onClick={saveDraft} disabled={!canEdit || isBusy}>
          Concept opslaan
        </button>
        <button type="button" onClick={submitForReview} disabled={!canReview || isBusy || documentId == null || activeVersionNumber == null}>
          Ter review
        </button>
        <button type="button" onClick={approve} disabled={!canApprove || isBusy || documentId == null || activeVersionNumber == null}>
          Goedkeuren
        </button>
        <p className="muted">{statusMessage}</p>
      </div>
    </section>
  );
}

async function callApi<T>(path: string, method: "POST", accessToken: string, payload: object): Promise<T> {
  const response = await fetch(path, {
    method,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`
    },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    let message = `API-aanroep mislukt (${response.status}).`;
    try {
      const body = await response.json() as { message?: string };
      if (body.message) {
        message = body.message;
      }
    } catch {
      // keep fallback message
    }
    throw new Error(message);
  }
  return response.json() as Promise<T>;
}
