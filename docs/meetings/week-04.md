| Key          | Value                                                                                          |
|--------------|------------------------------------------------------------------------------------------------|
| Date:        | 18-12-24                                                                                       |
| Time:        | 14:45 - 15:30                                                                                  |
| Location:    | DW PC1 Cubicle 2                                                                               |
| Chair        | Vasco Roldão                                                                                   |
| Minute Taker | Stijn van Drimmelen                                                                            |
| Attendees:   | Alexandru Grosu<br/>Hemant Anilkumar<br/>Iasonas Chronis<br/>Teodor Dragomir<br/>Amanda Andree |

# Agenda Items

## Opening by chair (1min)

## Announcements by the Team (3min)

- Errors with collections **IMPORTANT**
  - Occasionally crashes program
  - Issue lies in the default coded file
  - Hemant is working on it
- Iasonas has merge request for tags (which hasn't been aproved yet)

## Approval of the agenda - Does anyone have anything to add/alter? (1min)

## Approval of last minutes - Did everyone read the last minutes? (1min)

## Announcements by the TA (2min)

- Technology due friday 20 12 2024 (Meeting functionallity criteria)

## Presentation of the current app to the TA (4min)

Most epics have been started with (except language)

#### New features

- CSS implementation for webview
- Search bar, searches content if $ in front
- Note linking, with two [[ ]]
- Default collections, but has some issues

## Discuss what part of the epics is already done and what isn't (8min)

#### Embedded Content (Vasco)

- Backend almost implemented
- Frontend still has to be done

#### Interconnected Content (Sandry, Iasonas)

- Mostly functional
- Dependent on collections

#### Collections (Hemant)

- Working on frontend before backend
- *#BUG* Default collections causes crashes
- Bar on top left for collection

#### Basic (Stijn)

- Finishing up the basic requirements
- Added search functionality
- Added CSS implementation for webview

#### Linking (Teodor)

- Working on websockets

#### Ui Improvements (Teodor)

- Resizing and responsiveness almost done

## Discuss Alexandru's proposal of adding a cancel button when creating a new note (5min)

#### Problem

- Can't cancel creating note when no other notes are present

#### Solution

- Add cancel button to cancel creating note

## Discuss whether we should use the current search bar or just search for everything equally (5min)

#### Pros

- Independent filtering for more organized searching, especially in large notes
- More concise when filtering because can filter on tags, organization, title and content

#### Cons

- Normal user wont know to use dollar sign

#### Solution

- Add checkbox for content search instead of dollar sign

## Discuss what our priorities should be going forward (10min)

### Issues

#### Issue #1

Markdown write everything on one line, so no new line characters\
*Assigned to Vasco*

#### Issue #2

Can and if not should we be able to create tables with markdown

- Vasco will check if we can
- If we can't, discuss later if we want to implement this.

#### Issue #3

Viewing title in markdown webview

- Show title in different box above the content

#### Issue #4

Should the endpoint for collections and notes be different

- Problem for the future...

#### Issue #5

Remove all the final quote files\
*Assigned to Teodor*

#### Issue #6

Fix default collections bug **IMPORTANT**

- Why is the collection sometimes null
- How to fix

#### Issue #7

Creating new collections frontend **PRIORITY**

- Important for searching

### Agreements

#### Agreement #1

New UI features / objects should be in an anchorplane and correctly aligned so the program stays responsive.

#### Agreement #2

Who writes feature tests

1. People write their own tests
2. If no one has written test for a method, people who need the lines write them instead

## Summarize the meeting (2min)

## Questions(if any) (2min)

- Are we able to see amount of lines: we're not supposed to.
- When is next knockout criteria: last sunday of christmas break
- When do we get the Buddycheck responses: Today

## Closure (1min)
