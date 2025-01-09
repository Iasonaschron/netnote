| Key          | Value                                                                       |
|--------------|-----------------------------------------------------------------------------|
| Date:        | 18-12-24                                                                    |
| Time:        | 14:45 - 15:30                                                               |
| Location:    | DW PC1 Cubicle 2                                                            |
| Chair        | Alexandru Grosu                                                             |
| Minute Taker | Iasonas Chronis                                                             |
| Attendees:   | Amanda Andree<br/>Hemant Anilkumar<br/>Stijn van Drimmelen<br/>Vasco Roldão |

## Agenda Items:

### - Opening by chair (1min)
### - Check-in: How is everyone doing? (2 min)
### **Announcements by the Team (6 min)**

**Iasonas:**
- Finished **tag filtering feature**. 
- Added **tests** for adding and retrieving notes.

**Vasco:**
- Almost finished the **Embedded Content epic**.

**Stijn:**
- Almost finished the **Language Feature epic**.

**Teodor:**
- Started infrastructure for **web sockets**.
- Fixed the **default collection problem** that caused it to change to `null`.

**Alexandru:**
- Implemented **backend for language features**.
- Fixed **checkbox issue** for content searching.
- Added functionality to the **delete button** to act as a **clear button** when adding new notes.
### - Approval of the agenda - Does anyone have anything to add/alter? (1min)
- No changes or additions were made.
### - Approval of last minutes - Did everyone read the last minutes? (1min)
- Everyone confirmed they had read the last minutes.
### - Announcements by the TA (2min)
#### **Two formative deadlines this Friday!**
1. **Implemented features**.
2. **HCI considerations**.
### - Presentation of the current app to the TA (4min)
- Time allocated for showcasing the app to receive feedback from the TA.
### - Discuss what part of the epics is already done and what isn't (12min)
#### **Collections:**
- Progress: **Almost nothing done**.
- **Multi-collections** need to be implemented.
- Default collection implementation requires further checks.

#### **Embedded Content:**
- **Basically done**, but must ensure compatibility with **collections**.
- The **button** needs to be **resizable**.

#### **Interconnected Content:**
- Issues resolved, but compatibility with **collections** needs to be verified.

#### **Syncing:**
- Infrastructure is **complete** and the rest is **almost done**.

#### **Language:**
- **Almost complete**, pending the addition of **flags**.

#### **Testing:**
- Minimal progress.
- Suggestion: Explore using **TestFX** for testing.

**Summary:** While progress has started on all features, **none are fully complete**.
### - Discuss what our priorities should be going forward (12min)
#### **High Priority:**
- **Collection implementation** is critical; tasks need to be reorganized.
- Add **keyboard shortcuts**.
- Revisit **formative grades** to ensure no critical requirements are overlooked.
- **Reorganize files** into **business logic** and **surface classes**.
- Teodor to continue work on **surface classes**.
- Include **annotations** (e.g., `@Override`).
- Improve **merge request descriptions**, ensure **faster approvals**, and maintain better **organization**.

#### **Less Priority:**
- Use **Figma** to refine the **UI design**.
- Wait for an **available reviewer** before creating merge requests.
- Add more **inline comments** in the code.
- Refactor to reduce **redundancy** in the codebase by **splitting similar methods**.
### **Summarize the Meeting (2 min)**
- The team made significant progress on several features, but **collections implementation** remains a top priority.
- **Testing** needs more attention, with a suggestion to explore **TestFX**.
- **Formative deadlines** are approaching, and tasks need to be realigned to ensure completeness.
- Agreements were made for specific team members to address high-priority tasks and refine their respective areas.
### - Questions(if any) (2min)
### - Closure (1min)
### **Agreements:**
- **Stijn** will:
    - Explore **TestFX**.
    - Reorganize `NoteOverviewCtrl` by grouping related methods.
    - Review the UI and create a draft for implementation.

- **Iasonas** will:
    - Lay the foundation for **keyboard shortcuts** by referencing the rubric for specific requirements.

- **Teodor** will:
    - Ensure that all UI elements are **resizable**. 
