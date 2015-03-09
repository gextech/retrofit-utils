package gex.data.pagination

/**
 * Created by Tsunllly on 3/6/15.
 */
class PaginableHelper {
  
  static def executeSearch(Class<?> domainClass, PageParams pageParams, Boolean isCountOperation, Closure finder) {
    Integer from = pageParams.from
    Integer size = pageParams.size

    def c = domainClass.createCriteria()
    def results = c {

      if (!isCountOperation) {
        firstResult(from)
        maxResults(size)
      } else {
        projections {
          count('id')
        }
      }

      if (finder) {
        finder.setDelegate(c)
        finder.setResolveStrategy(Closure.DELEGATE_FIRST)
        finder.call(c)
      }
    }

    results
  }

  public static Map getPageCounts(Long total, Long from, Long size) {
    Long pageCount = Math.ceil(1.0f * total / size)
    Long currentPage = 1 + Math.ceil((from) / size)

    [
      total      : total,
      from       : from,
      size       : size,
      pageCount  : pageCount,
      currentPage: currentPage,
      hasNext    : currentPage < pageCount,
      hasPrev    : currentPage > 1
    ]

  }
}
