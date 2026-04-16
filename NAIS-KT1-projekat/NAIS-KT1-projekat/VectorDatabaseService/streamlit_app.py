import os
import requests
import streamlit as st

API_URL = os.getenv("API_URL", "http://vector-database-service:8000")

st.set_page_config(
    page_title="AI Assistant — Vector DB Demo",
    page_icon="🤖",
    layout="centered",
)

st.title("AI Assistant")
st.caption(f"Powered by Ollama · Vector DB: Milvus · API: {API_URL}")

# ── Ollama status badge ───────────────────────────────────────────────────────
try:
    health = requests.get(f"{API_URL}/api/v1/chat/health", timeout=3).json()
    if health.get("ollama_available"):
        st.success(f"LLM online — model: `{health.get('model')}`", icon="✅")
    else:
        st.warning("LLM not ready yet — Ollama may still be loading the model.", icon="⏳")
except Exception:
    st.error("Cannot reach the API. Make sure docker compose is running.", icon="🔴")

st.divider()

# ── Tabs ──────────────────────────────────────────────────────────────────────
tab_fashion, tab_science = st.tabs(["👗 Fashion Stylist", "🔬 Science Tutor"])


# ── Fashion tab ───────────────────────────────────────────────────────────────
with tab_fashion:
    st.subheader("Fashion Stylist")
    st.caption(
        "Ask me how to style an outfit. I search a real fashion catalog (1 000 items) "
        "in Milvus and ask an LLM to compose a recommendation from what's available."
    )

    if "fashion_history" not in st.session_state:
        st.session_state.fashion_history = []

    for msg in st.session_state.fashion_history:
        with st.chat_message(msg["role"]):
            st.markdown(msg["content"])
            if msg.get("context_items"):
                with st.expander("Items retrieved from catalog", expanded=False):
                    for item in msg["context_items"]:
                        st.markdown(f"- {item}")

    if prompt := st.chat_input(
        "e.g. What should I combine with a black t-shirt for a casual look?",
        key="fashion_input",
    ):
        st.session_state.fashion_history.append({"role": "user", "content": prompt})
        with st.chat_message("user"):
            st.markdown(prompt)

        with st.chat_message("assistant"):
            with st.spinner("Searching catalog and generating recommendation…"):
                try:
                    resp = requests.post(
                        f"{API_URL}/api/v1/fashion-lab/chat",
                        json={"message": prompt},
                        timeout=120,
                    )
                    resp.raise_for_status()
                    data = resp.json()
                    response_text = data["response"]
                    context_items = data.get("context_items", [])

                    st.markdown(response_text)
                    if context_items:
                        with st.expander("Items retrieved from catalog", expanded=False):
                            for item in context_items:
                                st.markdown(f"- {item}")
                except requests.exceptions.Timeout:
                    response_text = "⏳ Request timed out. The LLM may still be warming up — try again in a moment."
                    context_items = []
                    st.warning(response_text)
                except Exception as exc:
                    response_text = f"❌ Error: {exc}"
                    context_items = []
                    st.error(response_text)

        st.session_state.fashion_history.append({
            "role":          "assistant",
            "content":       response_text,
            "context_items": context_items,
        })

    if st.session_state.fashion_history:
        if st.button("Clear conversation", key="clear_fashion"):
            st.session_state.fashion_history = []
            st.rerun()


# ── Science tab ───────────────────────────────────────────────────────────────
with tab_science:
    st.subheader("Science Tutor")
    st.caption(
        "Ask any natural science question. I search ~500 passages from the SciQ dataset "
        "in Milvus (MiniLM embeddings) and ask an LLM to answer from what's found."
    )

    if "science_history" not in st.session_state:
        st.session_state.science_history = []

    for msg in st.session_state.science_history:
        with st.chat_message(msg["role"]):
            st.markdown(msg["content"])
            if msg.get("sources"):
                with st.expander("Retrieved passages", expanded=False):
                    for i, src in enumerate(msg["sources"], 1):
                        st.markdown(f"**Passage {i}:** {src}")

    if prompt := st.chat_input(
        "e.g. Why do objects fall at the same speed regardless of mass?",
        key="science_input",
    ):
        st.session_state.science_history.append({"role": "user", "content": prompt})
        with st.chat_message("user"):
            st.markdown(prompt)

        with st.chat_message("assistant"):
            with st.spinner("Searching passages and generating answer…"):
                try:
                    resp = requests.post(
                        f"{API_URL}/api/v1/sciq/chat",
                        json={"message": prompt},
                        timeout=120,
                    )
                    resp.raise_for_status()
                    data = resp.json()
                    response_text = data["response"]
                    sources = data.get("sources", [])

                    st.markdown(response_text)
                    if sources:
                        with st.expander("Retrieved passages", expanded=False):
                            for i, src in enumerate(sources, 1):
                                st.markdown(f"**Passage {i}:** {src}")
                except requests.exceptions.Timeout:
                    response_text = "⏳ Request timed out. The LLM may still be warming up — try again in a moment."
                    sources = []
                    st.warning(response_text)
                except Exception as exc:
                    response_text = f"❌ Error: {exc}"
                    sources = []
                    st.error(response_text)

        st.session_state.science_history.append({
            "role":     "assistant",
            "content":  response_text,
            "sources":  sources,
        })

    if st.session_state.science_history:
        if st.button("Clear conversation", key="clear_science"):
            st.session_state.science_history = []
            st.rerun()
