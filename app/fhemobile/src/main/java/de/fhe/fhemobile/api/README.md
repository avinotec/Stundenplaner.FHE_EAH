# Working with Moses

Moses is currently the API used to manage a wide variety of data from events to degree programs and
more.

Unfortunately, there is currently no 100% accurate OpenAPI specification file provided by the
vendor - I have done my best to create my own that matches the actual API responses. More about this
in the README.md in this directory under /openapi.

## Workflows

The following end points must be called up one after the other
| Step | Endpoint                                | Schema                                            | Purpose                                                                                                                                                                      | Comment                                                                                                                  |
|------|-----------------------------------------|---------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| 1    | `studiengang/`                          | `studiengang/`                                    | Select a study program and use its ID, "E-Commerce (B.Sc.)" is ID `7`                                                                                                        |                                                                                                                          |
| 2    | `vplgruppe/7`                           | `vplgruppe/{studiengangId}`                       | This will return multiple groups and their semester. Simplified: `2, 2, 7, 7, 4`. <br>A TreeSet is used to construct a sorted set of semesters without duplicates: `2, 4, 7` | This isn't ideal, an endpoint which returns all available<br>semesters should be requested from the vendor to be made.   |
| 3    | `vplgruppe/7/2`                         | `/vplgruppe/{studiengangId}/{semester}`           | Once a `studiengangId` (`7`) and a `semester` (`2`) have been picked, <br>study groups (also called sets) can be picked from a list of objects, `WIEC(BA)2.01` / `id: 743`   | Once again, a list or a `TreeSet` have to be created.                                                                    |
| 4    | `calveranstaltung?planungsgruppeId=743` | `calveranstaltung?planungsgruppeId={vplGruppeId}` | Use an id from the previous step to get all events for that specific group.                                                                                                  | Might not be the last step, <br>as this doesn't contain specific times for each event.                                   |
| 5    | `buchungsgruppe/47845`                  | `buchungsgruppe/{buchungsGruppeId}`               | this provides the actual data for a veranstaltung, so when and where and with whom                                                                                           | Attention(!) this endpoint doesn't provide a unified structure for events: `EINZELTERMIN` is different from 'BLOCKTERMIN'|
| 6    | `???`                                   | `???`                                             | -                                                                                                                                                                            |                                                                                                                          |

