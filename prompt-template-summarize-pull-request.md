# Prompt Template: Summarize Pull Request for Non-Technical Stakeholder

**Date:** 2026-09-09
**Author:** Gabor Veres — Software Engineer
**Project:** Learning project
**Model:** Claude via Codemie, Sonnet 4.5
**DIAL location:** [DIAL shared link or folder path]
**Committed location:** https://github.com/Veresgabi/doggy/tree/ai_pull-request-test

---

## Purpose

This prompt summarizes a pull request for a non-technical stakeholder. It is used in the review stage of the software
development lifecycle (SDLC) to provide a clear and concise overview of code changes, their purpose, and potential 
impacts.

---

## Variable Placeholders

| Placeholder         | Description                                     | Example value |
|---------------------|-------------------------------------------------|---------------|
| `{{pr_number}}`     | The number of the pull request to be summarized | 3             |

---

## Output Format Instruction

Return the summary in a structured format that includes the following sections:
Changes in one sentence, Purpose in one sentence, Impact in one sentence, and a list of Key Points. 
Use bullet points for the Key Points section. Ensure the summary is clear and concise, suitable for a non-technical 
stakeholder.

---

## Prompt Body

Summarize the pull request {{pr_number}} for a non-technical stakeholder.
Return the summary in a structured format that includes the following sections:
Changes in one sentence, Purpose in one sentence, Impact in one sentence, and a list of Key Points.
Use bullet points for the Key Points section. Ensure the summary is clear and concise, suitable for a non-technical
stakeholder.

---

## Test Run (Author)

**Input values used:**
- `{{pr_number}}` = 1

**Output quality:** [One sentence — was the output usable as-is, or did you revise?]

---

## Peer Review

**Reviewer:** [Name — Role]
**Date reviewed:** 2026-09-09
**Model used by reviewer:** Claude via Codemie, Sonnet 4.5

**Reviewer input values used:**
- `{{pr_number}}` = [value reviewer used]

| Review question                                                | Reviewer answer           |
|----------------------------------------------------------------|---------------------------|
| Could you run the template without asking the author anything? | Yes / No — [one sentence] |
| Was the output format what you expected?                       | Yes / No — [one sentence] |
| Would you use this template on your own work?                  | Yes / No — [one sentence] |
| One concrete improvement suggestion                            | [One sentence]            |

---

## Revision History

| Version | Date       | Change             | Author |
|---------|------------|--------------------|--------|
| 1.0     | YYYY-MM-DD | Initial commit     | [Name] |
| 1.1     | YYYY-MM-DD | Post-review update | [Name] |