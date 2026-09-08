# Model Selection Note

**Date:** 2026-09-08
**Author:** Gabor Veres — Software Engineer
**Project:** Learning project
**Task:** Creating a test plan for the current code base  
**Committed location:** https://github.com/Veresgabi/doggy/tree/ai_pull-request-test

---

## Evaluation Criteria

| # | Criterion         | Why it matters for this task                        |
|---|-------------------|-----------------------------------------------------|
| 1 | Output accuracy   | The project needs detailed test plan                |
| 2 | Format compliance | I prefer a readable, understandable response format |
| 3 | Token useage      | The cost optimization is also important             |

---

## Prompt Used

"Please create a test plan for the current code base"

---

## Output Comparison

### Model A: Sonnet 4.5
> "1. Unit Tests"
  "2. Integration Tests"
  "3. Component Tests"
  "4. Data Transfer Object Tests"
  "5. Edge Cases & Error Handling"
  "6. Performance Tests"
  "7. Security Tests"
  "8. Test Data Management"
  "9. Test Coverage Goals"
  "10. Test Execution Strategy"
  "11. Known Issues & Risks"
  "12. Implementation Priority"
  "13. Test Environment Setup"

### Model B: Haiku 4.5
> "1. Unit Tests"
  "2. Integration Tests"
  "3. Edge Cases & Validation"
  "4. Performance Tests"
  "5. Security Tests"
  "6. Test Dependencies & Tools"
  "7. Coverage Goals"


---

## Scorecard

| Criterion         | Model A score (1–3) | Model A evidence               | Model B score (1–3) | Model B evidence                              |
|-------------------|---------------------|--------------------------------|---------------------|-----------------------------------------------|
| Output accuracy   | 3                   | 13 chapter points of test plan | 1                   | 7 chapter points of test plan                 |
| Format compliance | 3                   | created a TEST_PLAN.md file    | 2                   | The test plan chapters are in the answer text |
| Token useage      | 2                   | 4.107 tokens                   | 3                   | 2.745                                         |
| **Total**         | 8                   |                                | 6                   |                                               |

---

## Decision

**Selected model:** Sonnet 4.5

**Rationale:** The Model A (Sonnet 4.5) won, because the output was more detailed and it generated a TEST_PLAN.md that was very readable, with a bit higher token useage. The Model B lost, because the test plan it proposed contained only 7 criteria.

---

## Active Constraint

**What could change this decision within 30 days:**
If my token budget cap would be decreased, I would prefer the cheaper model.

---

## Revision history

| Version | Date | Change |
|---------|------|--------|
| 1.0 | YYYY-MM-DD | Initial commit |